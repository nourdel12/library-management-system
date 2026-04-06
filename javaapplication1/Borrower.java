package javaapplication1;

import java.io.Serializable;
import java.util.List;

public class Borrower extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Borrower() {}

    public Borrower(int userId, String name, String email,String password) {
        super(userId, name, email,password);
    }

    // -------------------------------------------------------
    //       VIEW BORROWING HISTORY (Using manager)
    // -------------------------------------------------------
    public void viewBorrowingHistory(BorrowingManager manager) {
        List<Borrowing> all = manager.listBorrowings();

        System.out.println("Borrowing history for: " + getName());

        boolean found = false;
        for (Borrowing b : all) {
            if (b.getBorrower() != null &&
                b.getBorrower().getUserId() == this.userId)
            {
                System.out.println(b);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No borrowings found.");
        }
    }

    // -------------------------------------------------------
    //                 RATE A BOOK
    // -------------------------------------------------------
    public void rateBook(BorrowingManager manager, int borrowingId, double rating) {

        Borrowing b = manager.search(borrowingId);

        if (b == null) {
            System.out.println("Borrowing not found.");
            return;
        }

        if (b.getBorrower().getUserId() != this.userId) {
            System.out.println("You cannot rate a book you did not borrow.");
            return;
        }

        Book book = b.getBook();
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        if (rating < 0 || rating > 5) {
            System.out.println("Rating must be between 0 and 5.");
            return;
        }

        book.setRating(rating);
        System.out.println("You rated \"" + book.getTitle() +
                           "\" with " + rating + " stars.");
    }

    // -------------------------------------------------------
    //       OPTIONAL: BORROW A BOOK (using BorrowingManager)
    // -------------------------------------------------------
    public boolean borrowBook(BorrowingManager manager, Borrowing borrowing) {
        borrowing.setBorrower(this);
        return manager.createBorrowing(borrowing);
    }

    // -------------------------------------------------------
    //       OPTIONAL: RETURN BOOK
    // -------------------------------------------------------
    public boolean returnBook(BorrowingManager manager, int borrowingId) {
        Borrowing b = manager.search(borrowingId);

        if (b == null) {
            System.out.println("Borrowing not found.");
            return false;
        }

        if (b.getBorrower().getUserId() != this.userId) {
            System.out.println("You cannot return a book you did not borrow.");
            return false;
        }

        return manager.cancelBorrowing(borrowingId);
    }
}
