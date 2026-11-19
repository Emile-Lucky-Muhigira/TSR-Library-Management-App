package enums;

public enum BookType {
    PHYSICAL("Physical"), DIGITAL("Digital");
    private final String label;
    BookType(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
