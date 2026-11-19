import enums.BookStatus;
import enums.BookType;
import enums.SearchField;
import model.Book;
import service.BookService;
import service.ReportGenerator;
import storage.FileStorage;
import validation.InputValidator;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BookService service = new BookService();
    private static final FileStorage storage = new FileStorage();

    public static void main(String[] args) {
        try {
            service.restoreBooks(storage.load());
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not load library: " + e.getMessage());
            System.out.println("Check the saved file before restarting; it has not been overwritten.");
            scanner.close();
            return;
        }
        try {
            boolean running = true;
            while (running) {
                displayMenu();
                try {
                    switch (read("Enter your choice: ").trim()) {
                        case "1" -> addBook();
                        case "2" -> displayBooks(service.getAllBooks());
                        case "3" -> searchBooks();
                        case "4" -> borrowBook();
                        case "5" -> returnBook();
                        case "6" -> new ReportGenerator().generateReport(service.getAllBooks());
                        case "7" -> { if (save()) running = false; }
                        default -> System.out.println("Invalid choice. Enter a number from 1 to 7.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Input closed. Unsaved changes were not saved. Goodbye!");
        } finally {
            scanner.close();
        }
    }

    private static String read(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static void displayMenu() {
        System.out.println("==============================");
        System.out.println("        LIBTRACK v1.0");
        System.out.println("==============================");
        System.out.println("1. Add Book");
        System.out.println("2. View All Books");
        System.out.println("3. Search Books");
        System.out.println("4. Borrow a Book");
        System.out.println("5. Return a Book");
        System.out.println("6. Generate Report");
        System.out.println("7. Save & Exit");
        System.out.println("==============================");
    }

    private static void addBook() {
        String title = InputValidator.required(read("Title: "), "Title");
        String author = InputValidator.required(read("Author: "), "Author");
        String isbn = InputValidator.required(read("ISBN: "), "ISBN");
        String genre = read("Genre: ");
        int year = InputValidator.number(read("Year Published: "), "Year Published");
        BookType type;
        while (true) {
            try {
                type = InputValidator.type(read("Type (Physical or Digital): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        String details = read(type == BookType.PHYSICAL ? "Shelf location: " : "File format: ");
        Book book = service.addBook(title, author, isbn, genre, year, type, details);
        System.out.println("Book added successfully. ID: " + book.getId() + "; status: AVAILABLE.");
    }

    private static void displayBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-20s | %-15s | %-8s | %-9s | %-20s | %s%n",
                "ID", "Title", "Author", "Genre", "Type", "Status", "Shelf / Format", "Borrower");
        for (Book book : books) {
            System.out.printf("%-5d | %-25s | %-20s | %-15s | %-8s | %-9s | %-20s | %s%n",
                    book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), book.getType(),
                    book.getStatus(), book.getSpecificDetails(), book.getBorrowerName());
        }
    }

    private static void searchBooks() {
        System.out.println("Search by:\n1. Title\n2. Author\n3. Genre");
        SearchField field;
        while (true) {
            try {
                field = InputValidator.searchField(read("Choose search field: "));
                break;
            } catch (IllegalArgumentException e) { System.out.println(e.getMessage()); }
        }
        displayBooks(service.search(field, read("Keyword (leave empty for all): ")));
    }

    private static void borrowBook() {
        int id = InputValidator.number(read("Book ID: "), "Book ID");
        Book book = service.findById(id);
        if (book.getStatus() != BookStatus.AVAILABLE) {
            System.out.println("Book is " + book.getStatus() + "; borrower/reservation holder: "
                    + (book.getBorrowerName().isEmpty() ? "None recorded" : book.getBorrowerName()) + ".");
            return;
        }
        service.borrow(id, read("Borrower name: "));
        System.out.println("Book borrowed successfully by " + book.getBorrowerName() + ".");
    }

    private static void returnBook() {
        service.returnBook(InputValidator.number(read("Book ID: "), "Book ID"));
        System.out.println("Book returned successfully. Status: AVAILABLE.");
    }

    private static boolean save() {
        try {
            storage.save(service.getAllBooks());
            System.out.println("Library saved. Goodbye!");
            return true;
        } catch (IOException e) {
            System.out.println("Could not save library: " + e.getMessage());
            System.out.println("Your books remain in memory. Please retry Save & Exit.");
            return false;
        }
    }
}
