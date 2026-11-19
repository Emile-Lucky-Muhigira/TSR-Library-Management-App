package service;

import enums.BookType;
import enums.SearchField;
import model.Book;
import model.PhysicalBook;
import model.DigitalBook;
import validation.InputValidator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BookService {
    private final List<Book> books = new ArrayList<>();
    private long nextId = 1;

    public Book addBook(String title, String author, String isbn, String genre, int year,
                        BookType type, String details) {
        String validIsbn = InputValidator.required(isbn, "ISBN");
        if (books.stream().anyMatch(book -> book.getIsbn().equalsIgnoreCase(validIsbn))) {
            throw new IllegalArgumentException("A book with this ISBN already exists.");
        }
        if (type == null) throw new IllegalArgumentException("Book type is required.");
        if (nextId > Integer.MAX_VALUE) throw new IllegalArgumentException("No more book IDs are available.");
        Book book = type == BookType.PHYSICAL
                ? new PhysicalBook((int) nextId, title, author, validIsbn, genre, year, details)
                : new DigitalBook((int) nextId, title, author, validIsbn, genre, year, details);
        books.add(book);
        nextId++;
        return book;
    }

    public List<Book> getAllBooks() { return new ArrayList<>(books); }

    public Book findById(int id) {
        for (Book book : books) if (book.getId() == id) return book;
        throw new IllegalArgumentException("No book found with ID " + id + ".");
    }

    public void borrow(int id, String borrower) { findById(id).borrow(borrower); }
    public void returnBook(int id) { findById(id).returnBook(); }

    public List<Book> search(SearchField field, String keyword) {
        if (field == null) throw new IllegalArgumentException("Search field is required.");
        String query = keyword.toLowerCase(Locale.ROOT);
        List<Book> matches = new ArrayList<>();
        for (Book book : books) {
            String value = switch (field) {
                case TITLE -> book.getTitle();
                case AUTHOR -> book.getAuthor();
                case GENRE -> book.getGenre();
            };
            if (value.toLowerCase(Locale.ROOT).contains(query)) matches.add(book);
        }
        return matches;
    }

    public void restoreBooks(List<Book> saved) {
        Set<Integer> ids = new HashSet<>();
        Set<String> isbns = new HashSet<>();
        long candidateId = 1;
        for (Book book : saved) {
            if (!ids.add(book.getId()) || !isbns.add(book.getIsbn().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Duplicate saved ID or ISBN.");
            }
            candidateId = Math.max(candidateId, (long) book.getId() + 1);
        }
        books.clear();
        books.addAll(saved);
        nextId = candidateId;
    }
}
