package javaapplication1;

import java.util.Date;
import java.util.List;

public class AdminManager {

    private BookManager bookManager;
    private UserManager userManager;
    private SupplierManager supplierManager;
    private OrderManager orderManager;
    private BorrowingManager borrowingManager;

    public AdminManager(BookManager bookManager,
                        UserManager userManager,
                        SupplierManager supplierManager,
                        OrderManager orderManager,
                        BorrowingManager borrowingManager) {

        this.bookManager = bookManager;
        this.userManager = userManager;
        this.supplierManager = supplierManager;
        this.orderManager = orderManager;
        this.borrowingManager = borrowingManager;
    }

    // =========================================================================
    //                          BOOK MANAGEMENT
    // =========================================================================

    public boolean addBook(Book b) 
    {
        return bookManager.addBook(b);
    }

    public boolean editBook(int id, Book updated) 
    {
        return bookManager.editBook(id, updated);
    }

    public boolean removeBook(int id) 
    {
        return bookManager.removeBook(id);
    }

    public List<Book> listBooks() 
    {
        return bookManager.listBooks();
    }

    public Book searchBook(int id) 
    {
        return bookManager.searchBook(id);
    }

    // =========================================================================
    //                          USER MANAGEMENT
    // =========================================================================

    public boolean addUser(User u) {
        return userManager.addUser(u);
    }

    public boolean removeUser(int id) 
    {
        return userManager.removeUser(id);
    }

    public List<User> listUsers() 
    {
        return userManager.listUsers();
    }

    public User searchUser(int id)
    {
        return userManager.searchUser(id);
    }

    public boolean editUser(int id, User updated) 
    {
        User existing = userManager.searchUser(id);
        if (existing == null || updated == null)
            return false;

        // Update only editable fields present in your User class
        if (updated.getName() != null)
            existing.setName(updated.getName());

        if (updated.getEmail() != null)
            existing.setEmail(updated.getEmail());

        return true;
    }

    // =========================================================================
    //                       SUPPLIER MANAGEMENT
    // =========================================================================

    public boolean addSupplier(Supplier s) {
        return supplierManager.addSupplier(s);
    }

    public boolean removeSupplier(int id) {
        return supplierManager.removeSupplier(id);
    }

    public Supplier searchSupplier(int id) {
        return supplierManager.search(id);
    }

    public List<Supplier> listSuppliers() {
        return supplierManager.listSuppliers();
    }

    // =========================================================================
    //                           ORDER MANAGEMENT
    // =========================================================================

    public boolean placeOrder(int orderId, int supplierId, int bookId, int quantity) {
        Supplier s = supplierManager.search(supplierId);
        Book b = bookManager.searchBook(bookId);

        if (s == null || b == null)
            return false;

        Order o = new Order(orderId, s, b, quantity, new Date(), "Pending");
        return orderManager.placeOrder(o);
    }

    public boolean receiveOrder(int orderId) {
        return orderManager.receiveOrder(orderId, new Date());
    }

    public boolean cancelOrder(int orderId) {
        return orderManager.cancelOrder(orderId);
    }

    public List<Order> listOrders() {
        return orderManager.listOrders();
    }

    // =========================================================================
    //                           REPORT GENERATION
    // =========================================================================

    public void generateBookReport() {
        System.out.println("\n----- BOOK REPORT -----");
        for (Book b : bookManager.listBooks()) {
            System.out.println(b);
        }
    }

    public void generateUserReport() {
        System.out.println("\n----- USER REPORT -----");
        for (User u : userManager.listUsers()) {
            System.out.println(u);
        }
    }

    public void generateBorrowingReport() {
        System.out.println("\n----- BORROWING REPORT -----");
        for (Borrowing b : borrowingManager.listBorrowings()) {
            System.out.println(b);
        }
    }
}


