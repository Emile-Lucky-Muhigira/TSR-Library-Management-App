package service;

import enums.BookStatus;
import enums.BookType;
import model.Book;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Counts the books as they are now, not their borrowing history.
public class ReportGenerator {
    public String mostBorrowedGenre(List<Book> books) {
        // TreeMap keeps genres alphabetical and treats Fiction and fiction as the same genre.
        Map<String, Integer> counts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Book book : books) {
            if (book.getStatus() == BookStatus.BORROWED) {
                String genre = book.getGenre();
                if (genre.isEmpty()) genre = "(Unspecified)";
                int count = counts.getOrDefault(genre, 0);
                counts.put(genre, count + 1);
            }
        }
        String winner = "None";
        int highest = 0;
        // Keep the first alphabetical genre if two counts are equal.
        for (String genre : counts.keySet()) {
            int count = counts.get(genre);
            if (count > highest) {
                winner = genre;
                highest = count;
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
