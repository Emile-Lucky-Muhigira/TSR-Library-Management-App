package model;

import enums.BookType;

public class PhysicalBook extends Book {
    private final String shelfLocation;
    public PhysicalBook(int id, String title, String author, String isbn, String genre, int year, String shelf) {
        super(id, title, author, isbn, genre, year);
        shelfLocation = shelf == null ? "" : shelf.trim();
    }
    public String getShelfLocation() { return shelfLocation; }
    @Override public BookType getType() { return BookType.PHYSICAL; }
    @Override public String getSpecificDetails() { return shelfLocation; }
}
