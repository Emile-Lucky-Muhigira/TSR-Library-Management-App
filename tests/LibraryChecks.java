import enums.*;
import model.*;
import service.*;
import storage.FileStorage;
import validation.InputValidator;
import java.nio.file.*;
import java.util.*;

/** No testing framework or Python required. Each check throws on failure. */
public class LibraryChecks {
    private static int checks;
    private static void check(boolean result, String label) {
        if (!result) throw new AssertionError(label);
        checks++;
    }
    private static void rejects(Runnable action, String label) {
        try { action.run(); } catch (IllegalArgumentException e) { checks++; return; }
        throw new AssertionError(label);
    }
    private static Book add(BookService service, String isbn, BookType type, String genre) {
        return service.addBook("Java Basics", "Alice", isbn, genre, 2020, type, "A3 / PDF");
    }
    public static void main(String[] args) throws Exception {
        BookService service = new BookService();
        ReportGenerator report = new ReportGenerator();
        check(service.getAllBooks().isEmpty(), "Empty library");
        check(report.mostBorrowedGenre(service.getAllBooks()).equals("None"), "Empty report genre");
        Book physical = add(service, "ISBN-1", BookType.PHYSICAL, "Science");
        Book digital = add(service, "ISBN-2", BookType.DIGITAL, "Fiction");
        check(physical instanceof PhysicalBook && digital instanceof DigitalBook, "Distinct book models");
        check(physical.getStatus() == BookStatus.AVAILABLE && digital.getId() == 2, "Default status and IDs");
        rejects(() -> add(service, " isbn-1 ", BookType.DIGITAL, "Fiction"), "Duplicate ISBN");
        for (String[] fields : new String[][]{{"", "Author", "3"}, {"Title", " ", "3"}, {"Title", "Author", ""}}) {
            rejects(() -> service.addBook(fields[0], fields[1], fields[2], "", 2020, BookType.PHYSICAL, ""), "Required fields");
        }
        rejects(() -> InputValidator.number("year", "Year"), "Invalid year");
        rejects(() -> InputValidator.number("2000.5", "Year"), "Fractional year");
        rejects(() -> InputValidator.number("999999999999", "Year"), "Number overflow");
        rejects(() -> InputValidator.type("Other"), "Invalid type");
        check(InputValidator.type(" digital ") == BookType.DIGITAL, "Type case insensitive");
        check(service.search(SearchField.TITLE, "JAVA").size() == 2, "Title search");
        check(service.search(SearchField.AUTHOR, "ALICE").size() == 2, "Author search");
        check(service.search(SearchField.GENRE, "SCIENCE").size() == 1, "Genre search");
        check(service.search(SearchField.TITLE, "").size() == 2, "Empty search");
        check(service.search(SearchField.TITLE, "missing").isEmpty(), "No matches");
        rejects(() -> service.borrow(999, "Emile"), "Missing borrow ID");
        rejects(() -> service.returnBook(999), "Missing return ID");
        rejects(() -> service.borrow(1, " "), "Empty borrower");
        check(physical.getStatus() == BookStatus.AVAILABLE, "Failed borrowing leaves state unchanged");
        service.borrow(1, "Emile");
        check(physical.getStatus() == BookStatus.BORROWED && physical.getBorrowerName().equals("Emile"), "Borrow");
        rejects(() -> service.borrow(1, "Other"), "Already borrowed");
        check(report.mostBorrowedGenre(service.getAllBooks()).equals("Science"), "Current borrowed genre");
        service.borrow(2, "Second");
        check(report.mostBorrowedGenre(service.getAllBooks()).equals("Fiction"), "Alphabetical tie");
        service.returnBook(1);
        check(physical.getStatus() == BookStatus.AVAILABLE && physical.getBorrowerName().isEmpty(), "Return clears name");
        rejects(() -> service.returnBook(1), "Return available");
        Book reserved = add(service, "ISBN-3", BookType.PHYSICAL, "History");
        reserved.restoreState(BookStatus.RESERVED, "Reserved reader");
        rejects(() -> service.borrow(3, "Other"), "Reserved borrow");
        rejects(() -> service.returnBook(3), "Reserved return");
        Path directory = Files.createTempDirectory("libtrack-checks-");
        Path file = directory.resolve("books.properties");
        FileStorage storage = new FileStorage(file);
        try {
            check(storage.load().isEmpty(), "Missing file");
            storage.save(service.getAllBooks());
            List<Book> loaded = storage.load();
            check(loaded.size() == 3, "Reload count");
            for (int i = 0; i < loaded.size(); i++) {
                Book before = service.getAllBooks().get(i), after = loaded.get(i);
                check(before.getId() == after.getId() && before.getTitle().equals(after.getTitle())
                    && before.getAuthor().equals(after.getAuthor()) && before.getIsbn().equals(after.getIsbn())
                    && before.getGenre().equals(after.getGenre()) && before.getYearPublished() == after.getYearPublished()
                    && before.getType() == after.getType() && before.getStatus() == after.getStatus()
                    && before.getSpecificDetails().equals(after.getSpecificDetails())
                    && before.getBorrowerName().equals(after.getBorrowerName()), "Every field restored");
            }
            BookService restored = new BookService();
            restored.restoreBooks(loaded);
            check(add(restored, "ISBN-4", BookType.DIGITAL, "").getId() == 4, "Next ID after restart");
            Files.writeString(file, "count=invalid");
            try { storage.load(); throw new AssertionError("Corrupt file accepted"); }
            catch (java.io.IOException expected) { checks++; }
            check(Files.readString(file).equals("count=invalid"), "Corrupt file preserved");
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory.resolve("books.properties.tmp"));
            Files.deleteIfExists(directory);
        }
        System.out.println("Passed " + checks + " Java checks.");
    }
}
