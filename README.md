# LibTrack - Book Library Management System

**Author:** [Emile Lucky Muhigira](https://github.com/Emile-Lucky-Muhigira)

**Programme:** Quality Assurance Training for Starters - Java Programming

A Java 17 command-line application for managing physical and digital books, searching the library, recording borrowing and returns, and reporting current availability. Data is saved locally between sessions.

## Quick start

Install **JDK 17 or later**. Both `java` and `javac` must be available in your terminal. Git is needed for cloning; alternatively use GitHub's **Code > Download ZIP**, extract it, and open a terminal in the extracted project folder.

```sh
git clone https://github.com/Emile-Lucky-Muhigira/TSR-Library-Management-App.git
cd TSR-Library-Management-App
java -version
javac -version
javac -d out src/Main.java src/model/*.java src/enums/*.java src/validation/*.java src/storage/*.java src/service/*.java
java -cp out Main
```

Run these commands from the repository root. They work in Windows PowerShell and standard macOS/Linux shells. No Maven, Python, external libraries, or database are required. Recompile after editing Java source files.

To start again after compiling:

```sh
java -cp out Main
```

## Run in IntelliJ IDEA

1. Select **File > Open** and open this repository's folder.
2. In **File > Project Structure > Project**, choose JDK 17 or later as the Project SDK.
3. If needed, right-click `src` and choose **Mark Directory as > Sources Root**.
4. Open `src/Main.java` and click the green Run icon beside `main`.
5. Enter menu choices in the Run console. Set the run configuration's working directory to the project root so terminal and IDE runs use the same saved library.

## Menu and features

```text
==============================
        LIBTRACK v1.0
==============================
1. Add Book
2. View All Books
3. Search Books
4. Borrow a Book
5. Return a Book
6. Generate Report
7. Save & Exit
==============================
Enter your choice:
```

1. **Add Book:** enter title, author, unique ISBN, genre, publication year, and Physical/Digital type. Physical books store a shelf location; digital books store a file format. IDs are assigned automatically, and new books are AVAILABLE.
2. **View All Books:** displays ID, title, author, genre, type, status, shelf/format, and borrower. An empty library displays a message.
3. **Search Books:** select title, author, or genre, then enter a keyword. Matching is case-insensitive. An empty keyword returns all books; no matches displays a message.
4. **Borrow a Book:** enter an existing AVAILABLE book ID and a nonblank borrower name. The status changes to BORROWED. Unavailable books display their current status and recorded holder without changing anything.
5. **Return a Book:** enter a BORROWED book ID. The status becomes AVAILABLE and its borrower name is cleared. Other statuses and missing IDs produce an error.
6. **Generate Report:** displays total books, Physical/Digital counts, AVAILABLE/BORROWED/RESERVED counts, and the most borrowed genre. This genre is based on currently BORROWED books, not historical borrowing. An empty library displays zeros and `None`.
7. **Save & Exit:** saves the library and displays a farewell message.

The application tracks digital book metadata only; it does not open or distribute ebook files.

## Example first run

Choose `1` and enter:

```text
Title: Java Basics
Author: Alice Example
ISBN: LIB-001
Genre: Education
Year Published: 2024
Type: Physical
Shelf location: A3
```

The first book in a new library receives ID `1`. Choose `4`, enter `1`, and enter a borrower name. Choose `6` to see the report. Choose `7`, restart, and choose `2` to see the restored borrowed book. Choose `5` and enter its ID to return it.

## Saving and loading

`books.properties` is written in UTF-8 in the current working directory. Startup loads every saved field: ID, title, author, ISBN, genre, year, type, shelf/format, status, and borrower. Future IDs continue above existing IDs.

- A missing file starts an empty library.
- Choose **7. Save & Exit** to persist changes; stopping the app first loses unsaved changes.
- Saving first writes `books.properties.tmp`, then replaces the saved file.
- A save error leaves the app running so the user can retry.
- Invalid or unreadable saved data produces a message and stops startup without overwriting the file. Back up and inspect that file before repairing it.
- Saved data and compiled output are excluded from Git. Each user starts with their own library.

## Project structure

```text
TSR-Library-Management-App/
├── README.md
├── .gitignore
├── reports/
│   └── assignment-verification.md
├── tests/
│   └── LibraryChecks.java
└── src/
    ├── Main.java
    ├── model/
    │   ├── Book.java
    │   ├── PhysicalBook.java
    │   └── DigitalBook.java
    ├── enums/
    │   ├── BookStatus.java
    │   ├── BookType.java
    │   └── SearchField.java
    ├── validation/
    │   └── InputValidator.java
    ├── storage/
    │   └── FileStorage.java
    └── service/
        ├── BookService.java
        └── ReportGenerator.java
```

`Main` handles the menu and display. Models encapsulate book fields and borrowing rules; physical and digital books inherit from the abstract `Book`. Enums define fixed choices. `InputValidator` validates input. `BookService` handles the collection, ISBN uniqueness, IDs, and search. `ReportGenerator` calculates reports. `FileStorage` reads/writes Java Properties text; it does not use Java object serialization.

## Explicit interpretations of unspecified requirements

- **RESERVED:** the questionnaire requires this status, but provides no reservation menu. Reserved records can be restored, reported, and rejected for borrowing/returning. The seven menu choices remain as specified. A reservation holder, if present, is stored in the borrower field; otherwise the app displays `None recorded`.
- **Year:** interpreted as a whole number within Java's `int` range. No positive-year, past-year, or other historical restriction is specified or imposed.
- **ISBN:** nonblank and unique after trimming outer spaces and ignoring case. No ISBN length/checksum validation or internal hyphen normalization is specified.
- **Optional fields:** genre, shelf location, and file format are prompted for but are not required by the validation rules. No fixed format list is imposed.
- **Genre report:** genre labels differing only by case count together. Ties use alphabetical order ignoring case. Blank genres are shown as `(Unspecified)` when borrowed. No borrowed books means `None`.
- **Digital borrowing:** follows the same single-book borrowing rules as physical books, as required by the common borrowing workflow.

## Optional Java verification checks

From the project root:

```sh
javac -d out src/Main.java src/model/*.java src/enums/*.java src/validation/*.java src/storage/*.java src/service/*.java tests/LibraryChecks.java
java -cp out LibraryChecks
```

The checks use Java only, write temporary test data separately, and fail with an assertion if a check fails. See [assignment verification](reports/assignment-verification.md) for coverage and the live demonstration checklist.

## Troubleshooting

- **java/javac not found:** install a JDK, add its `bin` folder to PATH, and reopen the terminal.
- **Main class not found:** compile successfully, then run `java -cp out Main` from the project root.
- **Saved books seem missing:** check the working directory; each directory can have its own `books.properties` file.
- **No Python or Maven installed:** neither is needed to run the application or the included Java checks.

## Submission

The assignment requires a practical terminal demonstration and code review. The app and verification notes support preparation; the student/group must still demonstrate the features and explain the code.
