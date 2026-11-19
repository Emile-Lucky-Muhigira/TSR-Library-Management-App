package enums;

// The two kinds of book; labels are used in the menu and table.
public enum BookType {
    PHYSICAL("Physical"), DIGITAL("Digital");
    private final String label;
    BookType(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
