package javaapplication1;
import java.io.Serializable;

public class Admin extends User implements ReportGenerator, Serializable {

    private static final long serialVersionUID = 1L;

    public Admin() {}

    public Admin(int userId, String name, String email,String password) {
        super(userId, name, email,password);
    }

    // -----------------------------------------------------
    //                      BOOK MANAGEMENT
    // -----------------------------------------------------
    public boolean addBook(BookManager manager, Book book) {
        return manager.addBook(book);
    }

    public boolean editBook(BookManager manager, int bookId, Book updatedBook) {
        return manager.editBook(bookId, updatedBook);
    }

    public boolean removeBook(BookManager manager, int bookId) {
        return manager.removeBook(bookId);
    }

    public void listBooks(BookManager manager) {
        for (Book b : manager.listBooks()) {
            System.out.println(b);
        }
    }

    // -----------------------------------------------------
    //                      USER MANAGEMENT
    // -----------------------------------------------------
    public boolean addUser(UserManager userManager, User user) {
        return userManager.addUser(user);
    }

    public boolean editUser(UserManager userManager, int userId, User updatedUser) {
        return userManager.editUser(userId, updatedUser);
    }

    public boolean removeUser(UserManager userManager, int userId) {
        return userManager.removeUser(userId);
    }

    public void listUsers(UserManager userManager) {
        for (User u : userManager.listUsers()) {
            System.out.println(u);
        }
    }

    // -----------------------------------------------------
    //                     REPORT GENERATION
    // -----------------------------------------------------
    @Override
    public void generateBookReport(BookManager bookManager) {
        System.out.println("---- BOOK REPORT ----");
        for (Book b : bookManager.listBooks()) {
            System.out.println(b);
        }
    }

    @Override
    public void generateUserReport(UserManager userManager) {
        System.out.println("---- USER REPORT ----");
        for (User u : userManager.listUsers()) {
            System.out.println(u);
        }
    }

    @Override
    public void generateBorrowingReport(BorrowingManager borrowingManager) {
        System.out.println("---- BORROWING REPORT ----");
        for (Borrowing b : borrowingManager.listBorrowings()) {
            System.out.println(b);
        }
    }
}


