package storage;

import enums.BookStatus;
import enums.BookType;
import model.Book;
import model.PhysicalBook;
import model.DigitalBook;
import service.BookService;
import validation.InputValidator;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// Converts books to text properties when saving, and back to objects when loading.
public class FileStorage {
    private final Path file;
    public FileStorage() { this(Path.of("books.properties")); }
    public FileStorage(Path file) { this.file = file; }

    public void save(List<Book> books) throws IOException {
        Properties data = new Properties();
        data.setProperty("count", Integer.toString(books.size()));
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String prefix = "book." + i + ".";
            data.setProperty(prefix + "id", Integer.toString(book.getId()));
            data.setProperty(prefix + "title", book.getTitle());
            data.setProperty(prefix + "author", book.getAuthor());
            data.setProperty(prefix + "isbn", book.getIsbn());
            data.setProperty(prefix + "genre", book.getGenre());
            data.setProperty(prefix + "year", Integer.toString(book.getYearPublished()));
            data.setProperty(prefix + "type", book.getType().name());
            data.setProperty(prefix + "details", book.getSpecificDetails());
            data.setProperty(prefix + "status", book.getStatus().name());
            data.setProperty(prefix + "borrower", book.getBorrowerName());
        }
        // Finish writing before replacing the previous saved file.
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        // try-with-resources closes the file automatically, even if writing fails.
        try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
            data.store(writer, "LibTrack saved books");
        }
        Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
    }

    public List<Book> load() throws IOException {
        List<Book> books = new ArrayList<>();
        if (Files.notExists(file)) return books;
        Properties data = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            data.load(reader);
            int count = InputValidator.number(required(data, "count"), "Saved book count");
            if (count < 0) throw new IllegalArgumentException("Invalid saved book count.");
            for (int i = 0; i < count; i++) {
                String prefix = "book." + i + ".";
                int id = InputValidator.number(required(data, prefix + "id"), "Saved ID");
                String title = required(data, prefix + "title");
                String author = required(data, prefix + "author");
                String isbn = required(data, prefix + "isbn");
                String genre = required(data, prefix + "genre");
                int year = InputValidator.number(required(data, prefix + "year"), "Year Published");
                BookType type = BookType.valueOf(required(data, prefix + "type"));
                String details = required(data, prefix + "details");
                Book book;
                if (type == BookType.PHYSICAL) {
                    book = new PhysicalBook(id, title, author, isbn, genre, year, details);
                } else {
                    book = new DigitalBook(id, title, author, isbn, genre, year, details);
                }
                BookStatus status = BookStatus.valueOf(required(data, prefix + "status"));
                String borrower = required(data, prefix + "borrower");
                book.restoreState(status, borrower);
                books.add(book);
            }
            new BookService().restoreBooks(books); // Check duplicate IDs and ISBNs before accepting the file.
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid saved library: " + e.getMessage(), e);
        }
        return books;
    }

    private String required(Properties data, String key) {
        String value = data.getProperty(key);
        if (value == null) throw new IllegalArgumentException("Missing field: " + key);
        return value;
    }
}
