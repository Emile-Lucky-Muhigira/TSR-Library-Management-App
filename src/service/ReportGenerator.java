package service;

import enums.BookStatus;
import enums.BookType;
import model.Book;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportGenerator {
    public String mostBorrowedGenre(List<Book> books) {
        Map<String, Integer> counts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Book book : books) {
            if (book.getStatus() == BookStatus.BORROWED) {
                String genre = book.getGenre().isEmpty() ? "(Unspecified)" : book.getGenre();
                counts.merge(genre, 1, Integer::sum);
            }
        }
        String winner = "None";
        int highest = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > highest) {
                winner = entry.getKey();
                highest = entry.getValue();
            }
        }
        return winner;
    }

    public void generateReport(List<Book> books) {
        int physical = 0, available = 0, borrowed = 0, reserved = 0;
        for (Book book : books) {
            if (book.getType() == BookType.PHYSICAL) physical++;
            switch (book.getStatus()) {
                case AVAILABLE -> available++;
                case BORROWED -> borrowed++;
                case RESERVED -> reserved++;
            }
        }
        System.out.println("================================");
        System.out.println("        LIBRARY REPORT");
        System.out.println("================================");
        System.out.println("Total Books      : " + books.size());
        System.out.println("  - Physical     : " + physical);
        System.out.println("  - Digital      : " + (books.size() - physical));
        System.out.println("--------------------------------");
        System.out.println("AVAILABLE        : " + available);
        System.out.println("BORROWED         : " + borrowed);
        System.out.println("RESERVED         : " + reserved);
        System.out.println("--------------------------------");
        System.out.println("Most borrowed genre: " + mostBorrowedGenre(books));
        System.out.println("================================");
    }
}
