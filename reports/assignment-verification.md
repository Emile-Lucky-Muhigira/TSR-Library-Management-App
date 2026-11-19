# Assignment verification - LibTrack

**Date:** 19 November 2026  
**Reference:** `module_2_capstone_extended.pdf`, Java Project Assignment - Book Library Management System  
**Environment:** Windows, JDK 17.0.12

## Results

- Java compilation passed.
- 38 included Java checks passed (`tests/LibraryChecks.java`).
- Eight additional isolated terminal sessions passed, exercising the actual menu, validation prompts, save/restart, and a simulated save failure. These additional checks were run during development; the included repeatable suite is the Java check class.
- No existing QA Tracker project or saved test cases were modified.

## Requirement coverage

1. **Add Book:** physical/digital subclasses, automatic IDs, AVAILABLE default, all specified fields, and type-specific details implemented. Required title/author/ISBN, duplicate ISBN, invalid year, and invalid type checked.
2. **View All:** specified columns and type-specific details displayed, with a message for an empty library. Borrower is included as an additional column.
3. **Search:** selectable title/author/genre fields, case-insensitive matching, empty keyword returning all, and no-match message checked. Invalid search choices re-prompt.
4. **Borrow:** existing AVAILABLE book required; nonblank borrower stored and status changed to BORROWED. Missing ID, empty name, already borrowed, and reserved book rejection checked.
5. **Return:** existing BORROWED book required; status reset and borrower cleared. Missing, AVAILABLE, and RESERVED cases rejected.
6. **Report:** totals by type/status and most currently borrowed genre implemented. Empty result, current-borrowed calculation, and alphabetical tie handling checked.
7. **Save & Exit:** all book fields survive a save/load round trip, including borrowed and reserved states and holder names. IDs continue after restart. Missing file starts empty; corrupt data is rejected without being overwritten. Save failure keeps the menu available.

All 12 validation rules in the questionnaire are represented above. Additional checks cover numeric overflow, fractional publication years, case-insensitive type input, closed terminal input, and invalid menu/search choices.

## Design and assumptions

The Java structure follows the earlier QA Tracker approach: models, enums, validation, storage, services, and Main. It uses abstraction, inheritance, polymorphism, encapsulation, collections, exceptions, enums, and file I/O.

The questionnaire does not define a reservation action, ISBN format rules, year range, genre tie resolution, or whether optional metadata is mandatory. The README documents the implementation decisions. RESERVED is supported without adding an unrequested eighth menu option.

## Manual demonstration checklist

- [ ] Compile and start from the project root.
- [ ] Show an empty list and report with zeros and `None`.
- [ ] Reject blank title, author, and ISBN; reject a nonnumeric year.
- [ ] Reject an invalid type, then accept Physical/Digital.
- [ ] Add a physical and digital book; show both and their extra fields.
- [ ] Reject an ISBN already assigned to another book.
- [ ] Search each field using different capitalization, an empty keyword, and no-match keyword.
- [ ] Borrow an available book; reject blank borrower and nonexistent ID.
- [ ] Attempt to borrow it again; show status and borrower.
- [ ] Generate a report and explain that the genre count uses current loans.
- [ ] Save and exit; restart and show the same fields, borrower, and status.
- [ ] Return the borrowed book and verify the borrower is cleared.
- [ ] Attempt a second return and show the current-status message.
- [ ] Explain class responsibilities and the file reading/writing flow during code review.

The practical demonstration and code review are student/group submission activities and are not claimed as completed by these development checks.
