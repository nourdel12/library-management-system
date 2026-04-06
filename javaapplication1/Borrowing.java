package javaapplication1;

import java.io.Serializable;
import java.util.Date;

public class Borrowing implements Validatable, Serializable {

    private static final long serialVersionUID = 1L;

    private int borrowingId;
    private Book book;
    private Borrower borrower;
    private Librarian librarian;
    private Date borrowDate;
    private Date returnDate;
    private double paymentAmount;

    // ---------- Constructors ----------
    public Borrowing() {}

    public Borrowing(int borrowingId, Book book, Borrower borrower,
                     Librarian librarian, Date borrowDate, Date returnDate,
                     double paymentAmount) {

        this.borrowingId = borrowingId;
        this.book = book;
        this.borrower = borrower;
        this.librarian = librarian;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.paymentAmount = paymentAmount;
    }

    // ---------- Getters & Setters ----------
    public int getBorrowingId() {
        return borrowingId;
    }

    public void setBorrowingId(int borrowingId) {
        if (borrowingId > 0)
            this.borrowingId = borrowingId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        if (book != null)
            this.book = book;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        if (borrower != null)
            this.borrower = borrower;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        if (librarian != null)
            this.librarian = librarian;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        if (paymentAmount >= 0)
            this.paymentAmount = paymentAmount;
    }

    // ---------- VALIDATION ----------
    @Override
    public boolean validate() {

        if (borrowingId <= 0)
            return false;

        if (book == null || !book.validate())
            return false;

        if (borrower == null)
            return false;

        if (librarian == null)
            return false;

        if (borrowDate == null)
            return false;

        if (returnDate == null)
            return false;

        if (!returnDate.after(borrowDate))
            return false;

        if (paymentAmount < 0)
            return false;

        return true;
    }

    // ---------- PAYMENT CALCULATION ----------
    public double calculatePayment() {
        long diff = returnDate.getTime() - borrowDate.getTime();
        long days = diff / (1000 * 60 * 60 * 24);

        double pricePerDay = 10.0;
        paymentAmount = days * pricePerDay;
        return paymentAmount;
    }

    // ---------- toString ----------
    @Override
    public String toString() {
        return "Borrowing ID: " + borrowingId +
               ", Book: " + (book != null ? book.getTitle() : "N/A") +
               ", Borrower: " + (borrower != null ? borrower.getName() : "N/A") +
               ", Librarian: " + (librarian != null ? librarian.getName() : "N/A") +
               ", Borrow Date: " + borrowDate +
               ", Return Date: " + returnDate +
               ", Payment: " + paymentAmount;
    }
}

