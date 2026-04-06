package javaapplication1;

import java.util.Date;
import java.util.Scanner;

public class JavaApplication1 {

    private static final Scanner sc = new Scanner(System.in);

    // Managers
    private static final BookManager bookManager = new BookManager();
    private static final UserManager userManager = new UserManager();
    private static final BorrowingManager borrowingManager = new BorrowingManager();
    private static final SupplierManager supplierManager = new SupplierManager();
    private static final OrderManager orderManager = new OrderManager();

    public static void main(String[] args) {
        loadAll();

        while (true) {
            System.out.println("\n===== LIBRARY SYSTEM =====");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Librarian");
            System.out.println("3. Login as Borrower");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            int choice = readInt();

            switch (choice) {
                case 1 -> adminLogin();
                case 2 -> librarianLogin();
                case 3 -> borrowerLogin();
                case 4 -> {
                    saveAll();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ============================ LOGIN ============================

    private static void adminLogin() {
        System.out.print("Enter Admin User ID: ");
        User u = userManager.searchUser(readInt());

        if (!(u instanceof Admin)) {
            System.out.println("Admin not found.");
            return;
        }
        adminMenu((Admin) u);
    }

    private static void librarianLogin() {
        System.out.print("Enter Librarian User ID: ");
        User u = userManager.searchUser(readInt());

        if (!(u instanceof Librarian)) {
            System.out.println("Librarian not found.");
            return;
        }
        librarianMenu((Librarian) u);
    }

    private static void borrowerLogin() {
        System.out.print("Enter Borrower User ID: ");
        User u = userManager.searchUser(readInt());

        if (!(u instanceof Borrower)) {
            System.out.println("Borrower not found.");
            return;
        }
        borrowerMenu((Borrower) u);
    }

    // ============================ ADMIN MENU ============================

    private static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n----- ADMIN MENU -----");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage Users");
            System.out.println("3. Manage Suppliers");
            System.out.println("4. Manage Orders");
            System.out.println("5. Reports");
            System.out.println("6. Logout");

            System.out.print("Choose: ");
            int ch = readInt();

            switch (ch) {
                case 1 -> bookManagement();
                case 2 -> userManagement();
                case 3 -> supplierManagement();
                case 4 -> orderManagement();
                case 5 -> adminReports(admin);
                case 6 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ============================ LIBRARIAN MENU ============================

    private static void librarianMenu(Librarian lib) {
        while (true) {
            System.out.println("\n----- LIBRARIAN MENU -----");
            System.out.println("1. Create Borrowing");
            System.out.println("2. Cancel Borrowing");
            System.out.println("3. List Borrowings");
            System.out.println("4. Logout");

            System.out.print("Choose: ");
            int ch = readInt();

            switch (ch) {
                case 1 -> libCreateBorrowing(lib);
                case 2 -> libCancelBorrowing(lib);
                case 3 -> borrowingManager.displayBorrowings();
                case 4 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ============================ BORROWER MENU ============================

    private static void borrowerMenu(Borrower br) {
        while (true) {
            System.out.println("\n----- BORROWER MENU -----");
            System.out.println("1. Borrow a Book");
            System.out.println("2. Return a Book");
            System.out.println("3. View Borrowing History");
            System.out.println("4. Rate a Book");
            System.out.println("5. Logout");

            System.out.print("Choose: ");
            int ch = readInt();

            switch (ch) {
                case 1 -> borrowerBorrowBook(br);
                case 2 -> borrowerReturnBook(br);
                case 3 -> br.viewBorrowingHistory(borrowingManager);
                case 4 -> borrowerRateBook(br);
                case 5 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ============================ USER MANAGEMENT ============================

    private static void userManagement() {
        while (true) {
            System.out.println("\n--- USER MANAGEMENT ---");
            System.out.println("1. Add User");
            System.out.println("2. Edit User");
            System.out.println("3. Remove User");
            System.out.println("4. List Users");
            System.out.println("5. Back");
            System.out.print("Choose: ");

            int ch = readInt();

            switch (ch) {
                case 1 -> addUser();
                case 2 -> editUser();
                case 3 -> removeUser();
                case 4 -> userManager.displayUsers();
                case 5 -> { return; }
                default -> System.out.println("Invalid.");
            }
        }
    }

    private static void addUser() {
        System.out.print("User ID: ");
        int id = readInt(); sc.nextLine();

        System.out.print("Name: ");
        String name = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        System.out.println("Role: 1=Borrower, 2=Librarian, 3=Admin");
        int r = readInt();

        User u = switch (r) {
            case 1 -> new Borrower(id, name, email, pass);
            case 2 -> new Librarian(id, name, email, pass);
            case 3 -> new Admin(id, name, email, pass);
            default -> null;
        };

        if (u == null) {
            System.out.println("Invalid role.");
            return;
        }

        System.out.println(userManager.addUser(u) ? "User added." : "Failed.");
    }

    private static void editUser() {
        System.out.print("User ID: ");
        int id = readInt(); sc.nextLine();

        System.out.print("New Name: ");
        String name = sc.nextLine();

        System.out.print("New Email: ");
        String email = sc.nextLine();

        System.out.print("New Password: ");
        String pass = sc.nextLine();

        User updated = new Admin(id, name, email, pass);
        System.out.println(userManager.editUser(id, updated) ? "Updated." : "Failed.");
    }

    private static void removeUser() {
        System.out.print("User ID to remove: ");
        int id = readInt();
        System.out.println(userManager.removeUser(id) ? "Removed." : "Failed.");
    }

    // ============================ BOOK MANAGEMENT ============================

    private static void bookManagement() {
        while (true) {
            System.out.println("\n--- BOOK MANAGEMENT ---");
            System.out.println("1. Add Book");
            System.out.println("2. Edit Book");
            System.out.println("3. Remove Book");
            System.out.println("4. List Books");
            System.out.println("5. Back");
            System.out.print("Choose: ");

            int ch = readInt();

            switch (ch) {
                case 1 -> {
                    System.out.print("Book ID: ");
                    int id = readInt(); sc.nextLine();

                    System.out.print("Title: ");
                    String title = sc.nextLine();

                    System.out.print("Quantity: ");
                    int q = readInt();

                    Book b = new Book(id, title, null, q, 0, 0.0);
                    System.out.println(bookManager.addBook(b)
                            ? "Book added."
                            : "Error adding book.");
                }

                case 2 -> {
                    System.out.print("Book ID to edit: ");
                    int id = readInt(); 
                    sc.nextLine();

                    System.out.print("New Title: ");
                    String t = sc.nextLine();

                    System.out.print("New Quantity: ");
                    int q = readInt();

                    Book updated = new Book(id, t, null, q, 0, 0.0);
                    System.out.println(bookManager.editBook(id, updated)
                            ? "Updated."
                            : "Failed.");
                }

                case 3 -> {
                    System.out.print("Book ID to remove: ");
                    int id = readInt();
                    System.out.println(bookManager.removeBook(id)
                            ? "Removed."
                            : "Failed.");
                }

                case 4 -> bookManager.displayBooks();

                case 5 -> { return; }

                default -> System.out.println("Invalid.");
            }
        }
    }

    // ============================ SUPPLIER MANAGEMENT ============================

    private static void supplierManagement() {
        while (true) {
            System.out.println("\n--- SUPPLIER MANAGEMENT ---");
            System.out.println("1. Add Supplier");
            System.out.println("2. List Suppliers");
            System.out.println("3. Back");

            int ch = readInt();

           switch (ch) {
             case 1 -> {
    System.out.print("Supplier ID: ");
    int sid = readInt();
    sc.nextLine();

    System.out.print("Supplier Name: ");
    String name = sc.nextLine();

    System.out.print("Contact Info: ");
    String contact = sc.nextLine();

    Supplier s = new Supplier(sid, name, contact);

    System.out.println(
        supplierManager.addSupplier(s)
            ? "Supplier added."
            : "Failed."
    );
}


                case 2 -> supplierManager.displaySuppliers();

                case 3 -> { return; }

                default -> System.out.println("Invalid.");
            }
        }
    }

    // ============================ ORDER MANAGEMENT ============================

    private static void orderManagement() {
        while (true) {
            System.out.println("\n--- ORDER MANAGEMENT ---");
            System.out.println("1. Place Order");
            System.out.println("2. Receive Order");
            System.out.println("3. Cancel Order");
            System.out.println("4. List Orders");
            System.out.println("5. Back");

            System.out.print("Choose: ");
            int ch = readInt();

            switch (ch) {
                case 1 -> {
                    System.out.print("Order ID: ");
                    int id = readInt();

                    System.out.print("Supplier ID: ");
                    int sid = readInt();

                    System.out.print("Book ID: ");
                    int bid = readInt();

                    System.out.print("Quantity: ");
                    int q = readInt();

                    Supplier s = supplierManager.search(sid);
                    Book b = bookManager.searchBook(bid);

                    if (s == null || b == null) {
                        System.out.println("Supplier or book not found.");
                        break;
                    }

                    Order o = new Order(id, s, b, q, new Date(), "Pending");

                    System.out.println(orderManager.placeOrder(o)
                            ? "Order placed."
                            : "Failed.");
                }

                case 2 -> {
                    System.out.print("Order ID: ");
                    int id = readInt();

                    boolean ok = orderManager.receiveOrder(id, new Date());
                    System.out.println(ok ? "Order received." : "Failed.");
                }

                case 3 -> {
                    System.out.print("Order ID: ");
                    int id = readInt();

                    boolean ok = orderManager.cancelOrder(id);
                    System.out.println(ok ? "Order cancelled." : "Failed.");
                }

                case 4 -> orderManager.listOrders().forEach(System.out::println);

                case 5 -> { return; }

                default -> System.out.println("Invalid.");
            }
        }
    }

    // ============================ BORROWING FUNCTIONS ============================

    private static void libCreateBorrowing(Librarian lib) {
        System.out.print("Borrowing ID: ");
        int id = readInt();

        System.out.print("Book ID: ");
        int bid = readInt();

        System.out.print("Borrower ID: ");
        int uid = readInt();

        System.out.print("Days: ");
        int days = readInt();

        Book book = bookManager.searchBook(bid);
        User u = userManager.searchUser(uid);

        if (!(u instanceof Borrower)) {
            System.out.println("Not a borrower.");
            return;
        }

        Date now = new Date();
        Date ret = new Date(now.getTime() + (long) days * 24 * 60 * 60 * 1000);

        Borrowing b = new Borrowing(id, book, (Borrower) u, lib, now, ret, 0.0);

        System.out.println(borrowingManager.createBorrowing(b)
                ? "Created."
                : "Failed.");
    }

    private static void libCancelBorrowing(Librarian lib) {
        System.out.print("Borrowing ID: ");
        int id = readInt();

        System.out.println(borrowingManager.cancelBorrowing(id)
                ? "Cancelled."
                : "Failed.");
    }

    private static void borrowerBorrowBook(Borrower borrower) {
        System.out.print("Borrowing ID: ");
        int id = readInt();

        System.out.print("Book ID: ");
        int bid = readInt();

        System.out.print("Days: ");
        int days = readInt();

        Book book = bookManager.searchBook(bid);

        if (book == null || book.getQuantity() <= 0) {
            System.out.println("Book not available.");
            return;
        }

        Date now = new Date();
        Date ret = new Date(now.getTime() + (long) days * 24 * 60 * 60 * 1000);

        Borrowing b = new Borrowing(id, book, borrower, null, now, ret, 0.0);

        System.out.println(borrower.borrowBook(borrowingManager, b)
                ? "Borrowed."
                : "Failed.");
    }

    private static void borrowerReturnBook(Borrower borrower) {
        System.out.print("Borrowing ID: ");
        int id = readInt();

        System.out.println(borrower.returnBook(borrowingManager, id)
                ? "Returned."
                : "Failed.");
    }

    private static void borrowerRateBook(Borrower borrower) {
        System.out.print("Borrowing ID: ");
        int id = readInt();

        System.out.print("Rating (0-5): ");
        double r = sc.nextDouble();

        borrower.rateBook(borrowingManager, id, r);
    }

    // ============================ REPORTS ============================

    private static void adminReports(Admin admin) {
        System.out.println("\n--- REPORTS ---");
        admin.generateBookReport(bookManager);
        admin.generateUserReport(userManager);
        admin.generateBorrowingReport(borrowingManager);
    }

    // ============================ UTIL & SAVE/LOAD ============================

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private static void loadAll() {
        bookManager.loadFromFile("books.dat");
        userManager.loadFromFile("users.dat");
        borrowingManager.loadFromFile("borrow.dat");
        supplierManager.loadFromFile("suppliers.dat");
        orderManager.loadFromFile("orders.dat");
    }

    private static void saveAll() {
        bookManager.saveToFile("books.dat");
        userManager.saveToFile("users.dat");
        borrowingManager.saveToFile("borrow.dat");
        supplierManager.saveToFile("suppliers.dat");
        orderManager.saveToFile("orders.dat");
    }
        
}

