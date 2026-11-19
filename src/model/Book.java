package model;

import enums.BookStatus;
import enums.BookType;
import validation.InputValidator;

// Parent class: every physical or digital book has these fields and borrowing rules.
public abstract class Book {
    // final fields are set once, when the book is created.
    private final int id;
    private final String title;
    private final String author;
    private final String isbn;
    private final String genre;
    private final int yearPublished;
    private BookStatus status = BookStatus.AVAILABLE;
    private String borrowerName = "";

    protected Book(int id, String title, String author, String isbn, String genre, int year) {
        if (id <= 0) throw new IllegalArgumentException("Book ID must be positive.");
        this.id = id;
        this.title = InputValidator.required(title, "Title");
        this.author = InputValidator.required(author, "Author");
        this.isbn = InputValidator.required(isbn, "ISBN");
        this.genre = genre == null ? "" : genre.trim();
        this.yearPublished = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }
    public int getYearPublished() { return yearPublished; }
    public BookStatus getStatus() { return status; }
    public String getBorrowerName() { return borrowerName; }
    public abstract BookType getType();
    public abstract String getSpecificDetails();

    public void borrow(String name) {
        if (status != BookStatus.AVAILABLE) {
            throw new IllegalArgumentException(unavailableMessage());
        }
        borrowerName = InputValidator.required(name, "Borrower name");
        status = BookStatus.BORROWED;
    }

    // Used by both the menu and the borrowing rule, so they show the same message.
    public String unavailableMessage() {
        String holder = borrowerName;
        if (holder.isEmpty()) holder = "None recorded";
        return "Book is " + status + "; borrower/reservation holder: " + holder + ".";
    }

    public void returnBook() {
        if (status != BookStatus.BORROWED) {
            throw new IllegalArgumentException("Book is " + status + "; only borrowed books can be returned.");
        }
        status = BookStatus.AVAILABLE;
        borrowerName = "";
    }

    /** Restore persisted state, including RESERVED (no reservation menu is specified). */
    public void restoreState(BookStatus savedStatus, String savedName) {
        if (savedStatus == null) throw new IllegalArgumentException("Status is required.");
        String name = savedName == null ? "" : savedName.trim();
        if (savedStatus == BookStatus.BORROWED) name = InputValidator.required(name, "Borrower name");
        if (savedStatus == BookStatus.AVAILABLE && !name.isEmpty()) {
            throw new IllegalArgumentException("An available book cannot have a borrower.");
        }
        status = savedStatus;
        borrowerName = name;
    }
}
