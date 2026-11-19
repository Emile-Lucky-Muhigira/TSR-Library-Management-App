package model;

import enums.BookType;

public class DigitalBook extends Book {
    private final String fileFormat;
    public DigitalBook(int id, String title, String author, String isbn, String genre, int year, String format) {
        super(id, title, author, isbn, genre, year);
        fileFormat = format == null ? "" : format.trim();
    }
    public String getFileFormat() { return fileFormat; }
    @Override public BookType getType() { return BookType.DIGITAL; }
    @Override public String getSpecificDetails() { return fileFormat; }
}
