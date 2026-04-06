/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.io.Serializable;
import java.util.Date;

public class Librarian extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Librarian() {}

    public Librarian(int userId, String name, String email,String password) {
        super(userId, name, email,password);
    }

    // ------------------------------------------------------------
    //                  CREATE BORROWING
    // ------------------------------------------------------------
    public boolean createBorrowing(BorrowingManager manager, Borrowing borrowing) {
        if (borrowing == null) {
            System.out.println("Invalid borrowing request.");
            return false;
        }

        // Attach this librarian to the borrowing
        borrowing.setLibrarian(this);

        return manager.createBorrowing(borrowing);
    }

    // ------------------------------------------------------------
    //                  CANCEL BORROWING
    // ------------------------------------------------------------
    public boolean cancelBorrowing(BorrowingManager manager, int borrowingId) {
        return manager.cancelBorrowing(borrowingId);
    }

    // ------------------------------------------------------------
    //                  CALCULATE PAYMENT
    // ------------------------------------------------------------
    public double calculatePayment(BorrowingManager manager, int borrowingId) {
        return manager.calculatePayment(borrowingId);
    }

    // ------------------------------------------------------------
    //                  SPECIFY BORROW TERM
    // ------------------------------------------------------------
    public void specifyBorrowTerm(Borrowing borrowing, Date borrowDate, Date returnDate) {

        if (borrowing == null) {
            System.out.println("Borrowing not found.");
            return;
        }

        if (borrowDate == null || returnDate == null) {
            System.out.println("Dates cannot be null.");
            return;
        }

        if (!returnDate.after(borrowDate)) {
            System.out.println("Return date must be after borrow date.");
            return;
        }

        borrowing.setBorrowDate(borrowDate);
        borrowing.setReturnDate(returnDate);

        System.out.println("Borrow term set: " + borrowDate + " to " + returnDate);
    }

}
