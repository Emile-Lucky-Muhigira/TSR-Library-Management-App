package model;

import enums.BookType;

// A digital book adds a file format to the shared Book fields.
public class DigitalBook extends Book {
    private final String fileFormat;
    public DigitalBook(int id, String title, String author, String isbn, String genre, int year, String format) {
        // Let Book set up the common fields first.
        super(id, title, author, isbn, genre, year);
        fileFormat = format == null ? "" : format.trim();
    }
    public String getFileFormat() { return fileFormat; }
    @Override public BookType getType() { return BookType.DIGITAL; }
    @Override public String getSpecificDetails() { return fileFormat; }
}
