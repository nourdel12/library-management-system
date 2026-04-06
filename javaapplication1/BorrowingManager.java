package javaapplication1;

import java.io.*;
import java.util.*;

public class BorrowingManager implements FilePersistable {

    private List<Borrowing> borrowings = new ArrayList<>();

    // ---------------------- CREATE ----------------------
    public boolean createBorrowing(Borrowing borrowing) {

        if (borrowing == null || !borrowing.validate()) {
            System.out.println("Invalid borrowing. Cannot create.");
            return false;
        }

        // prevent duplicate IDs
        if (search(borrowing.getBorrowingId()) != null) {
            System.out.println("Borrowing ID already exists.");
            return false;
        }

        // borrower must not be null
        if (borrowing.getBorrower() == null) {
            System.out.println("Borrower is required.");
            return false;
        }

        // librarian must not be null
        if (borrowing.getLibrarian() == null) {
            System.out.println("Librarian is required.");
            return false;
        }

        // book must not be null and must be available
        Book book = borrowing.getBook();
        if (book == null) {
            System.out.println("Book cannot be null.");
            return false;
        }

        if (book.getQuantity() <= 0) {
            System.out.println("Book is out of stock.");
            return false;
        }

        // decrease quantity + increment borrow count
        book.decreaseQuantity();
        book.incrementBorrowCount();

        borrowings.add(borrowing);
        return true;
    }

    // ---------------------- SEARCH BY ID ----------------------
    public Borrowing search(int borrowingId) {
        for (Borrowing b : borrowings) {
            if (b.getBorrowingId() == borrowingId)
                return b;
        }
        return null;
    }

    // ---------------------- SEARCH BY BORROWER ----------------------
    public List<Borrowing> searchByBorrower(int borrowerId) {
        List<Borrowing> result = new ArrayList<>();
        for (Borrowing b : borrowings) {
            if (b.getBorrower() != null &&
                b.getBorrower().getUserId() == borrowerId) {
                result.add(b);
            }
        }
        return result;
    }

    // ---------------------- SEARCH BY BOOK ----------------------
    public List<Borrowing> searchByBook(int bookId) {
        List<Borrowing> result = new ArrayList<>();
        for (Borrowing b : borrowings) {
            if (b.getBook() != null &&
                b.getBook().getBookId() == bookId) {
                result.add(b);
            }
        }
        return result;
    }

    // ---------------------- LIST ----------------------
    public List<Borrowing> listBorrowings() {
        return borrowings;
    }

    public void displayBorrowings() {
        for (Borrowing b : borrowings) {
            System.out.println(b);
        }
    }

    // ---------------------- CANCEL ----------------------
    public boolean cancelBorrowing(int borrowingId) {
        Borrowing b = search(borrowingId);

        if (b == null) {
            System.out.println("Borrowing not found.");
            return false;
        }

        // restore book quantity
        Book book = b.getBook();
        if (book != null) {
            book.increaseQuantity();
        }

        borrowings.remove(b);
        return true;
    }

    // ---------------------- PAYMENT ----------------------
    public double calculatePayment(int borrowingId) {
        Borrowing b = search(borrowingId);
        if (b == null) return -1;

        return b.calculatePayment();
    }

    // ---------------------- SAVE ----------------------
    @Override
    public void saveToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(borrowings);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // ---------------------- LOAD ----------------------
    @Override
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            borrowings = (List<Borrowing>) in.readObject();

            // remove invalid records after loading
            borrowings.removeIf(b -> !b.validate());

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    // ---------------------- UTILITIES ----------------------
    public int count() {
        return borrowings.size();
    }

    public void clear() {
        borrowings.clear();
    }

    public boolean isEmpty() {
        return borrowings.isEmpty();
    }

    // sort safely by borrow date
    public void sortByBorrowDate() {
        borrowings.sort(Comparator.comparing(
            b -> b.getBorrowDate() == null ? new Date(0) : b.getBorrowDate()
        ));
    }
}

