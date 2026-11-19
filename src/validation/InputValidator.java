package validation;

import enums.BookType;
import enums.SearchField;

public final class InputValidator {
    private InputValidator() { }

    public static String required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }

    public static int number(String value, String field) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(field + " must be a valid whole number.");
        }
    }

    public static BookType type(String value) {
        for (BookType type : BookType.values()) {
            if (value != null && type.toString().equalsIgnoreCase(value.trim())) return type;
        }
        throw new IllegalArgumentException("Please enter Physical or Digital.");
    }

    public static SearchField searchField(String value) {
        return switch (value.trim()) {
            case "1" -> SearchField.TITLE;
            case "2" -> SearchField.AUTHOR;
            case "3" -> SearchField.GENRE;
            default -> throw new IllegalArgumentException("Choose 1, 2, or 3.");
        };
    }
}
