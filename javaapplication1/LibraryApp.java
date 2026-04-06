package javaapplication1;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LibraryApp.java
 *
 * Modern animated JavaFX GUI (Option B) integrating with existing managers:
 * BookManager, UserManager, BorrowingManager, SupplierManager, OrderManager.
 *
 * Important: This file assumes your project provides the standard methods:
 * - bookManager.listBooks(), bookManager.searchBook(id), bookManager.addBook(...), bookManager.editBook(...)
 * - userManager.listUsers(), userManager.login(email,password), userManager.addUser(), userManager.editUser(), userManager.removeUser()
 * - borrowingManager.listBorrowings() etc.
 *
 * If method names differ slightly, adapt small calls accordingly.
 */
public class LibraryApp extends Application {

    // ----- managers (your existing classes) -----
    private final BookManager bookManager = new BookManager();
    private final UserManager userManager = new UserManager();
    private final BorrowingManager borrowingManager = new BorrowingManager();
    private final SupplierManager supplierManager = new SupplierManager();
    private final OrderManager orderManager = new OrderManager();

    // ----- UI state -----
    private Stage primaryStage;
    private User currentUser;

    // Observable lists used in tables
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final ObservableList<Supplier> supplierList = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();
    private final ObservableList<Borrowing> borrowingList = FXCollections.observableArrayList();

    // For reuse across UI
    private BorderPane mainRoot;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Library Management System — Modern UI");

        // load data (managers responsible for files)
        loadAll();
        refreshAllObservable();

        // ensure an admin exists so login is possible
        if (userManager.listUsers().isEmpty()) {
            Admin defaultAdmin = new Admin(1, "Administrator", "admin@library.com", "admin");
            userManager.addUser(defaultAdmin);
            userManager.saveToFile("users.dat");
            refreshUsers();
        }

        showLoginScene();

        primaryStage.setOnCloseRequest(e -> {
            saveAll();
            Platform.exit();
        });

        primaryStage.show();
    }

    // ------------------------ LOGIN SCENE ------------------------
    private void showLoginScene() {
        // gradient background
        Stop[] stops = new Stop[] {
            new Stop(0, Color.web("#5A60E7")),
            new Stop(1, Color.web("#60C1F5"))
        };
        LinearGradient bg = new LinearGradient(0,0,1,1,true, CycleMethod.NO_CYCLE, stops);

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(bg, CornerRadii.EMPTY, Insets.EMPTY)));

        // decorative circles
        Circle c1 = new Circle(120, Color.web("#ffffff", 0.06));
        c1.setTranslateX(-350);
        c1.setTranslateY(-200);
        Circle c2 = new Circle(180, Color.web("#ffffff", 0.05));
        c2.setTranslateX(380);
        c2.setTranslateY(220);

        VBox card = new VBox(18);
        card.setPadding(new Insets(28));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(520);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 16;");
        card.setEffect(new DropShadow(20, Color.rgb(0,0,0,0.25)));

        Label title = new Label("Library Management System");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        title.setTextFill(Color.WHITE);

        // email input
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleAuthInput(emailField);

        // password: PasswordField + TextField trick
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        styleAuthInput(passField);

        TextField passShown = new TextField();
        passShown.setManaged(false);
        passShown.setVisible(false);
        passShown.setPromptText("Password");
        styleAuthInput(passShown);

        passField.textProperty().bindBidirectional(passShown.textProperty());

        HBox showRow = new HBox(8);
        showRow.setAlignment(Pos.CENTER_LEFT);
        CheckBox showPass = new CheckBox("Show password");
        showPass.setTextFill(Color.WHITE);
        showPass.selectedProperty().addListener((obs, oldV, newV) -> {
            if (newV) {
                passShown.setVisible(true);
                passShown.setManaged(true);
                passField.setVisible(false);
                passField.setManaged(false);
            } else {
                passShown.setVisible(false);
                passShown.setManaged(false);
                passField.setVisible(true);
                passField.setManaged(true);
            }
        });
        showRow.getChildren().add(showPass);

        Label err = new Label();
        err.setTextFill(Color.web("#ff7b7b"));

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(360);
        loginBtn.setPrefHeight(44);
        loginBtn.setStyle("-fx-background-color: white; -fx-text-fill: #3E4BD8; -fx-background-radius: 22; -fx-font-size: 15;");
        loginBtn.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.2)));

        // handle Enter key on password
        passField.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) loginBtn.fire();
        });
        passShown.setOnKeyPressed(k -> {
            if (k.getCode() == KeyCode.ENTER) loginBtn.fire();
        });

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = passField.isVisible() ? passField.getText() : passShown.getText();
            if (email.isEmpty() || pass.isEmpty()) {
                err.setText("Enter both email and password.");
                animateShake(card);
                return;
            }
            User u = userManager.login(email, pass);
            if (u == null) {
                err.setText("Invalid credentials.");
                animateShake(card);
                return;
            }
            currentUser = u;
            refreshAllObservable();
            // small fade transition into main scene
            FadeTransition ft = new FadeTransition(Duration.millis(350), root);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(ev -> showMainScene());
            ft.play();
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        exitBtn.setOnAction(e -> {
            saveAll();
            Platform.exit();
        });

        card.getChildren().addAll(title, emailField, passField, passShown, showRow, loginBtn, err, exitBtn);
        root.getChildren().addAll(c1, c2, card);

        Scene scene = new Scene(root, 980, 620);
        primaryStage.setScene(scene);
    }

    // ------------------------ MAIN SCENE ------------------------
    private void showMainScene() {
        mainRoot = new BorderPane();
        mainRoot.setPadding(new Insets(10));

        // Top: Menu bar + user info
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(12);
        topBar.setPadding(new Insets(6));

        // Simple menu area
        MenuBar mb = new MenuBar();
        Menu fileMenu = new Menu("File");     //BUTTON FILE
        MenuItem importItem = new MenuItem("Import Data...");
        MenuItem openDir = new MenuItem("Open Data Folder");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem logoutItem = new MenuItem("Logout");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(importItem, openDir, new SeparatorMenuItem(), saveItem, logoutItem, new SeparatorMenuItem(), exitItem);
        mb.getMenus().add(fileMenu);

        importItem.setOnAction(e -> importDataFile());
        openDir.setOnAction(e -> openDataFolder());
        saveItem.setOnAction(e -> {
            saveAll();
            showInfo("Saved", "Data saved.");
        });
        logoutItem.setOnAction(e -> { currentUser = null; showLoginScene(); });

        exitItem.setOnAction(e -> {
            saveAll();
            Platform.exit();
        });

        Label userLbl = new Label("Signed in: " + currentUser.getName() + " (" + getUserRoleName(currentUser) + ")");
        userLbl.setPadding(new Insets(6));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(mb, spacer, userLbl);

        // Left: Sidebar (animated)
        VBox sidebar = new VBox(12);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(240);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #2E3AA8, #4B61D8); -fx-background-radius: 12;");
        sidebar.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.2)));

        Label brand = new Label("📚 Library");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        brand.setTextFill(Color.WHITE);

        Button btnDashboard = makeSidebarButton("Dashboard");
        Button btnBooks = makeSidebarButton("Books");
        Button btnUsers = makeSidebarButton("Users");
        Button btnSuppliers = makeSidebarButton("Suppliers");
        Button btnOrders = makeSidebarButton("Orders");
        Button btnBorrowings = makeSidebarButton("Borrowings");
        Button btnExplorer = makeSidebarButton("Explorer");
        Button btnLogout = makeSidebarButton("Logout");

        btnLogout.setOnAction(e -> {
            saveAll();
            currentUser = null;
            showLoginScene();
        });

        sidebar.getChildren().addAll(brand, new Separator(), btnDashboard, btnBooks, btnUsers, btnSuppliers, btnOrders, btnBorrowings, new Separator(), btnExplorer, btnLogout);

        // Center: content area (start with dashboard)
        StackPane centerPane = new StackPane();
        centerPane.setPadding(new Insets(12));
        Node dashboard = buildDashboard();
        centerPane.getChildren().add(dashboard);

        // Wire sidebar buttons to content
        btnDashboard.setOnAction(e -> animateReplace(centerPane, buildDashboard()));
        btnBooks.setOnAction(e -> animateReplace(centerPane, bookManagementPane()));
        btnUsers.setOnAction(e -> animateReplace(centerPane, userManagementPane()));
        btnSuppliers.setOnAction(e -> animateReplace(centerPane, supplierPane()));
        btnOrders.setOnAction(e -> animateReplace(centerPane, ordersPane()));
        btnBorrowings.setOnAction(e -> animateReplace(centerPane, borrowingPane()));
        btnExplorer.setOnAction(e -> openDataFolder());

        mainRoot.setTop(topBar);
        mainRoot.setLeft(sidebar);
        mainRoot.setCenter(centerPane);

        Scene scene = new Scene(mainRoot, 1250, 780);
        primaryStage.setScene(scene);
    }

    // ------------------------ DASHBOARD ------------------------
    private Node buildDashboard() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(12));

        Label heading = new Label("Dashboard");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));

        HBox cards = new HBox(16);
        cards.getChildren().addAll(
                makeStatCard("Books", String.valueOf(bookManager.listBooks().size()), Color.web("#5A60E7")),
                makeStatCard("Users", String.valueOf(userManager.listUsers().size()), Color.web("#34A0A4")),
                makeStatCard("Borrowings", String.valueOf(borrowingManager.listBorrowings().size()), Color.web("#F3722C")),
                makeStatCard("Orders", String.valueOf(orderManager.listOrders().size()), Color.web("#577590"))
        );

        // recent activity (simple)
        ListView<String> recent = new ListView<>();
        recent.getItems().addAll(
                "System loaded.",
                "Total books: " + bookManager.listBooks().size(),
                "Total users: " + userManager.listUsers().size(),
                "Active borrowings: " + borrowingManager.listBorrowings().size()
        );
        recent.setPrefHeight(220);

        HBox quick = new HBox(12);
        Button addBook = new Button("Add Book");
        addBook.setOnAction(e -> showAddBookDialog(null));
        Button addUser = new Button("Add User");
        addUser.setOnAction(e -> showAddUserDialog(null));
        Button openFolder = new Button("Open Data Folder");
        openFolder.setOnAction(e -> openDataFolder());
        quick.getChildren().addAll(addBook, addUser, openFolder);

        root.getChildren().addAll(heading, cards, new Label("Recent Activity"), recent, new Label("Quick Actions"), quick);
        return root;
    }

    private VBox makeStatCard(String title, String value, Color color) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12));
        box.setPrefSize(220, 120);
        String colorCss = toRgbString(color);
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, " + colorCss + ", white); -fx-background-radius: 12; -fx-border-radius:12;");
        box.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.14)));
        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", 14));
        Label v = new Label(value);
        v.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        box.getChildren().addAll(t, v);
        return box;
    }

    // ------------------------ BOOK MANAGEMENT ------------------------
    private Node bookManagementPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        HBox searchRow = new HBox(8);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by id or title...");
        Button btnSearch = new Button("Search");
        Button btnClear = new Button("Clear");
        searchRow.getChildren().addAll(searchField, btnSearch, btnClear);

        TableView<Book> table = new TableView<>(bookList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getBookId()));
        idCol.setPrefWidth(80);

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTitle()));

        TableColumn<Book, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategory() == null ? "N/A" : cd.getValue().getCategory().getName()));

        TableColumn<Book, Number> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getQuantity()));
        qtyCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, titleCol, catCol, qtyCol);

        HBox actions = new HBox(10);
        Button btnAdd = new Button("Add");
        Button btnEdit = new Button("Edit");
        Button btnDelete = new Button("Delete");
        Button btnRefresh = new Button("Refresh");
        actions.getChildren().addAll(btnAdd, btnEdit, btnDelete, btnRefresh);

        btnSearch.setOnAction(e -> {
            String s = searchField.getText().trim();
            if (s.isEmpty()) { refreshBooks(); return; }
            try {
                int id = Integer.parseInt(s);
                Book b = bookManager.searchBook(id);
                if (b != null) table.getItems().setAll(b);
                else table.getItems().clear();
            } catch (NumberFormatException ex) {
                table.getItems().setAll(bookManager.listBooks().stream()
                        .filter(b -> b.getTitle().toLowerCase().contains(s.toLowerCase()))
                        .collect(Collectors.toList()));
            }
        });
        btnClear.setOnAction(e -> { searchField.clear(); refreshBooks(); });

        btnAdd.setOnAction(e -> showAddBookDialog(table));
        btnEdit.setOnAction(e -> {
            Book sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Select a book to edit."); return; }
            showEditBookDialog(sel, table);
        });
        btnDelete.setOnAction(e -> {
            Book sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Select a book to delete."); return; }
            if (!confirm("Delete Book", "Delete '" + sel.getTitle() + "'?")) return;
            bookManager.removeBook(sel.getBookId());
            saveAll();  
            refreshBooks();
        });
        btnRefresh.setOnAction(e -> refreshBooks());

        root.getChildren().addAll(new Label("Books Management"), searchRow, table, actions);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private void showAddBookDialog(TableView<Book> table) {
        Dialog<Book> dlg = new Dialog<>();
        dlg.setTitle("Add Book");

        GridPane g = new GridPane();
        g.setHgap(8);
        g.setVgap(8);
        g.setPadding(new Insets(12));

        TextField idF = new TextField();
        TextField titleF = new TextField();
        TextField qtyF = new TextField();
        TextField ratingF = new TextField();
        TextField catIdF = new TextField();
        TextField catNameF = new TextField();

        g.add(new Label("ID:"), 0, 0); g.add(idF, 1, 0);
        g.add(new Label("Title:"), 0, 1); g.add(titleF, 1, 1);
        g.add(new Label("Qty:"), 0, 2); g.add(qtyF, 1, 2);
        g.add(new Label("Rating (0-5):"), 0, 3); g.add(ratingF, 1, 3);
        g.add(new Label("Category ID:"), 0, 4); g.add(catIdF, 1, 4);
        g.add(new Label("Category Name:"), 0, 5); g.add(catNameF, 1, 5);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(idF.getText().trim());
                    String title = titleF.getText().trim();
                    int qty = Integer.parseInt(qtyF.getText().trim());
                    double rate = Double.parseDouble(ratingF.getText().trim());
                    int cid = Integer.parseInt(catIdF.getText().trim());
                    String cname = catNameF.getText().trim();
                    Category cat = new Category(cid, cname);
                    return new Book(id, title, cat, qty, 0, rate);
                } catch (Exception ex) {
                    showError("Invalid input.");
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(b -> {
            boolean ok = bookManager.addBook(b);
            if (!ok) showError("Failed to add (duplicate ID or invalid).");
            saveAll(); 
            refreshBooks();
        });
    }

    private void showEditBookDialog(Book book, TableView<Book> table) {
        Dialog<Book> dlg = new Dialog<>();
        dlg.setTitle("Edit Book");
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));

        TextField titleF = new TextField(book.getTitle());
        TextField qtyF = new TextField(String.valueOf(book.getQuantity()));
        TextField ratingF = new TextField(String.valueOf(book.getRating()));

        g.add(new Label("Title:"), 0, 0); g.add(titleF, 1, 0);
        g.add(new Label("Qty:"), 0, 1); g.add(qtyF, 1, 1);
        g.add(new Label("Rating:"), 0, 2); g.add(ratingF, 1, 2);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    Book updated = new Book();
                    updated.setBookId(book.getBookId());
                    updated.setTitle(titleF.getText().trim());
                    updated.setCategory(book.getCategory());
                    updated.setQuantity(Integer.parseInt(qtyF.getText().trim()));
                    updated.setRating(Double.parseDouble(ratingF.getText().trim()));
                    updated.setTotalBorrowCount(book.getTotalBorrowCount());
                    return updated;
                } catch (Exception ex) {
                    showError("Invalid input.");
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(updated -> {
            boolean ok = bookManager.editBook(book.getBookId(), updated);
            if (!ok) showError("Failed to edit.");
            saveAll(); 
            refreshBooks();
        });
    }

    // ------------------------ USER MANAGEMENT ------------------------
    private Node userManagementPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));

        TableView<User> table = new TableView<>(userList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<User, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getUserId()));
        idCol.setPrefWidth(80);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail()));

        TableColumn<User, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cd -> new SimpleStringProperty(getUserRoleName(cd.getValue())));

        table.getColumns().addAll(idCol, nameCol, emailCol, typeCol);

        HBox actions = new HBox(10);
        Button btnAdd = new Button("Add");
        Button btnEdit = new Button("Edit");
        Button btnRemove = new Button("Remove");
        Button btnRefresh = new Button("Refresh");
        actions.getChildren().addAll(btnAdd, btnEdit, btnRemove, btnRefresh);

        btnAdd.setOnAction(e -> showAddUserDialog(table));
        btnEdit.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Select a user to edit."); return; }
            showEditUserDialog(sel, table);
        });
        btnRemove.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Select a user to remove."); return; }
            if (!confirm("Remove User", "Remove " + sel.getName() + "?")) return;
            userManager.removeUser(sel.getUserId());
            saveAll();  
            refreshUsers();
        });
        btnRefresh.setOnAction(e -> refreshUsers());

        root.getChildren().addAll(new Label("User Management"), table, actions);
        VBox.setVgrow(table, Priority.ALWAYS);
        return root;
    }

    private void showAddUserDialog(TableView<User> table) {
        Dialog<User> dlg = new Dialog<>();
        dlg.setTitle("Add User");
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));

        TextField idF = new TextField();
        TextField nameF = new TextField();
        TextField emailF = new TextField();

        PasswordField passF = new PasswordField();
        TextField passVisible = new TextField();
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        passVisible.textProperty().bindBidirectional(passF.textProperty());
        CheckBox showPass = new CheckBox("Show");
        showPass.setOnAction(e -> {
            boolean show = showPass.isSelected();
            passVisible.setManaged(show);
            passVisible.setVisible(show);
            passF.setManaged(!show);
            passF.setVisible(!show);
        });

        ComboBox<String> type = new ComboBox<>(FXCollections.observableArrayList("Borrower", "Librarian", "Admin"));
        type.setValue("Borrower");

        g.add(new Label("ID:"), 0, 0); g.add(idF, 1, 0);
        g.add(new Label("Name:"), 0, 1); g.add(nameF, 1, 1);
        g.add(new Label("Email:"), 0, 2); g.add(emailF, 1, 2);
        g.add(new Label("Password:"), 0, 3); g.add(passF, 1, 3); g.add(passVisible, 1, 3); g.add(showPass, 2, 3);
        g.add(new Label("Type:"), 0, 4); g.add(type, 1, 4);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int id = Integer.parseInt(idF.getText().trim());
                    String nm = nameF.getText().trim();
                    String em = emailF.getText().trim();
                    String pw = passF.getText();
                    String t = type.getValue();
                    User u;
                    switch (t) {
                        case "Admin": u = new Admin(id, nm, em, pw); break;
                        case "Librarian": u = new Librarian(id, nm, em, pw); break;
                        default: u = new Borrower(id, nm, em, pw);
                    }
                    return u;
                } catch (Exception ex) {
                    showError("Invalid input.");
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(u -> {
            if (!userManager.addUser(u)) showError("Failed to add (duplicate id or invalid).");
            saveAll(); 
            refreshUsers();
        });
    }

    private void showEditUserDialog(User user, TableView<User> table) {
        Dialog<User> dlg = new Dialog<>();
        dlg.setTitle("Edit User");
        GridPane g = new GridPane();
        g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));

        TextField nameF = new TextField(user.getName());
        TextField emailF = new TextField(user.getEmail());
        PasswordField passF = new PasswordField();
        TextField passVisible = new TextField();
        passVisible.setManaged(false);
        passVisible.setVisible(false);
        passVisible.textProperty().bindBidirectional(passF.textProperty());
        CheckBox showPass = new CheckBox("Show");
        showPass.setOnAction(e -> {
            boolean show = showPass.isSelected();
            passVisible.setManaged(show);
            passVisible.setVisible(show);
            passF.setManaged(!show);
            passF.setVisible(!show);
        });

        g.add(new Label("Name:"), 0, 0); g.add(nameF, 1, 0);
        g.add(new Label("Email:"), 0, 1); g.add(emailF, 1, 1);
        g.add(new Label("Password:"), 0, 2); g.add(passF, 1, 2); g.add(passVisible, 1, 2); g.add(showPass, 2, 2);

        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String newPass = passF.getText().isEmpty() ? user.getPassword() : passF.getText();
                // using Admin object as a generic holder for fields (your editUser only uses name/email/password)
                return new Admin(user.getUserId(), nameF.getText().trim(), emailF.getText().trim(), newPass);
            }
            return null;
        });

        dlg.showAndWait().ifPresent(updated -> {
            userManager.editUser(user.getUserId(), updated);
            saveAll(); 
            refreshUsers();
        });
    }

    // ------------------------ SUPPLIERS / ORDERS / BORROWINGS ------------------------
    private Node supplierPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        ListView<String> list = new ListView<>();
        refreshSuppliersList(list);

        Button add = new Button("Add Supplier");
        add.setOnAction(e -> {
            Dialog<Supplier> dlg = new Dialog<>();
            dlg.setTitle("Add Supplier");
            GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));
            TextField idF = new TextField(); TextField nameF = new TextField(); TextField contactF = new TextField();
            g.add(new Label("ID:"), 0, 0); g.add(idF, 1, 0);
            g.add(new Label("Name:"), 0, 1); g.add(nameF, 1, 1);
            g.add(new Label("Contact:"), 0, 2); g.add(contactF, 1, 2);
            dlg.getDialogPane().setContent(g);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dlg.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    try {
                        int id = Integer.parseInt(idF.getText().trim());
                        return new Supplier(id, nameF.getText().trim(), contactF.getText().trim());
                    } catch (Exception ex) { showError("Invalid input."); }
                }
                return null;
            });
            dlg.showAndWait().ifPresent(s -> {
                if (!supplierManager.addSupplier(s)) showError("Failed to add supplier.");
                saveAll();  
                refreshSuppliersList(list);
            });
        });

        root.getChildren().addAll(new Label("Suppliers"), list, add);
        VBox.setVgrow(list, Priority.ALWAYS);
        return root;
    }

    private Node ordersPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        ListView<String> list = new ListView<>();
        refreshOrdersList(list);

        Button place = new Button("Place Order");
        place.setOnAction(e -> showPlaceOrderDialog(list));

        Button receive = new Button("Receive Order");
        receive.setOnAction(e -> {
            TextInputDialog dia = new TextInputDialog();
            dia.setTitle("Receive Order"); dia.setHeaderText("Enter Order ID to receive:");
            dia.showAndWait().ifPresent(s -> {
                try {
                    int id = Integer.parseInt(s.trim());
                    boolean ok = orderManager.receiveOrder(id, new Date());
                    if (!ok) showError("Receive failed.");
                    saveAll();  
                    refreshOrdersList(list);
                    refreshBooks();
                } catch (Exception ex) { showError("Invalid ID."); }
            });
        });

        root.getChildren().addAll(new Label("Orders"), list, new HBox(8, place, receive));
        VBox.setVgrow(list, Priority.ALWAYS);
        return root;
    }

    private void showPlaceOrderDialog(ListView<String> list) {
        Dialog<Order> dlg = new Dialog<>();
        dlg.setTitle("Place Order");
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));
        TextField idF = new TextField(); TextField supIdF = new TextField(); TextField bookIdF = new TextField(); TextField qtyF = new TextField();
        g.add(new Label("Order ID:"), 0, 0); g.add(idF, 1, 0);
        g.add(new Label("Supplier ID:"), 0, 1); g.add(supIdF, 1, 1);
        g.add(new Label("Book ID:"), 0, 2); g.add(bookIdF, 1, 2);
        g.add(new Label("Qty:"), 0, 3); g.add(qtyF, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int oid = Integer.parseInt(idF.getText().trim());
                    int sid = Integer.parseInt(supIdF.getText().trim());
                    int bid = Integer.parseInt(bookIdF.getText().trim());
                    int q = Integer.parseInt(qtyF.getText().trim());
                    Supplier s = supplierManager.search(sid);
                    Book b = bookManager.searchBook(bid);
                    if (s == null || b == null) { showError("Supplier or Book not found."); return null; }
                    return new Order(oid, s, b, q, new Date(), "Pending");
                } catch (Exception ex) { showError("Invalid input."); }
            }
            return null;
        });
        dlg.showAndWait().ifPresent(o -> {
            if (!orderManager.placeOrder(o)) showError("Place order failed.");
            saveAll();  
            refreshOrdersList(list);
        });
    }

    private Node borrowingPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        TableView<Borrowing> tbl = new TableView<>(borrowingList);

        TableColumn<Borrowing, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getBorrowingId()));

        TableColumn<Borrowing, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBook() != null ? cd.getValue().getBook().getTitle() : "N/A"));

        TableColumn<Borrowing, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getBorrower() != null ? cd.getValue().getBorrower().getName() : "N/A"));

        TableColumn<Borrowing, Date> borrowDateCol = new TableColumn<>("Borrow Date");
        borrowDateCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getBorrowDate()));

        TableColumn<Borrowing, Date> returnDateCol = new TableColumn<>("Return Date");
        returnDateCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getReturnDate()));

        tbl.getColumns().addAll(idCol, bookCol, borrowerCol, borrowDateCol, returnDateCol);

        Button create = new Button("Create");
        Button cancel = new Button("Cancel");
        Button refresh = new Button("Refresh");
        create.setOnAction(e -> showCreateBorrowingDialog(tbl));
        cancel.setOnAction(e -> {
            Borrowing sel = tbl.getSelectionModel().getSelectedItem();
            if (sel == null) { showError("Select borrowing to cancel."); return; }
            if (!confirm("Cancel borrowing", "Cancel this borrowing?")) return;
            if (!borrowingManager.cancelBorrowing(sel.getBorrowingId())) showError("Cancel failed.");
            saveAll();  
            refreshBorrowings(); 
            
            refreshBooks();
        });
        refresh.setOnAction(e -> refreshBorrowings());

        root.getChildren().addAll(new Label("Borrowings"), tbl, new HBox(8, create, cancel, refresh));
        VBox.setVgrow(tbl, Priority.ALWAYS);
        return root;
    }

    private void showCreateBorrowingDialog(TableView<Borrowing> tbl) {
        Dialog<Borrowing> dlg = new Dialog<>();
        dlg.setTitle("Create Borrowing");
        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(12));
        TextField idF = new TextField(); TextField bookIdF = new TextField(); TextField borrowerIdF = new TextField(); TextField daysF = new TextField("7");
        g.add(new Label("Borrow ID:"), 0, 0); g.add(idF, 1, 0);
        g.add(new Label("Book ID:"), 0, 1); g.add(bookIdF, 1, 1);
        g.add(new Label("Borrower ID:"), 0, 2); g.add(borrowerIdF, 1, 2);
        g.add(new Label("Days:"), 0, 3); g.add(daysF, 1, 3);
        dlg.getDialogPane().setContent(g);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    int bid = Integer.parseInt(idF.getText().trim());
                    int bookId = Integer.parseInt(bookIdF.getText().trim());
                    int borrowerId = Integer.parseInt(borrowerIdF.getText().trim());
                    int days = Integer.parseInt(daysF.getText().trim());
                    Book book = bookManager.searchBook(bookId);
                    User u = userManager.searchUser(borrowerId);
                    if (book == null || u == null || !(u instanceof Borrower)) { showError("Invalid book or borrower."); return null; }
                    Date now = new Date();
                    Date ret = new Date(now.getTime() + (long) days * 24 * 60 * 60 * 1000);
                    Librarian lib = currentUser instanceof Librarian ? (Librarian) currentUser : null;
                    Borrowing b = new Borrowing(bid, book, (Borrower) u, lib, now, ret, 0.0);
                    return b;
                } catch (Exception ex) { showError("Invalid input."); }
            }
            return null;
        });
        dlg.showAndWait().ifPresent(b -> {
            if (!borrowingManager.createBorrowing(b)) showError("Create borrowing failed.");
            saveAll(); 
            refreshBorrowings(); refreshBooks();
        });
    }

    // ------------------------ HELPERS: refresh/load/save ------------------------
    private void refreshAllObservable() {
        refreshBooks();
        refreshUsers();
        refreshSuppliers();
        refreshOrders();
        refreshBorrowings();
    }

    private void refreshBooks() { bookList.setAll(bookManager.listBooks()); }
    private void refreshUsers() { userList.setAll(userManager.listUsers()); }
    private void refreshSuppliers() { supplierList.setAll(supplierManager.listSuppliers()); }
    private void refreshOrders() { orderList.setAll(orderManager.listOrders()); }
    private void refreshBorrowings() { borrowingList.setAll(borrowingManager.listBorrowings()); }

    private void loadAll() {
        try { bookManager.loadFromFile("books.dat"); } catch (Exception ignored) {}
        try { userManager.loadFromFile("users.dat"); } catch (Exception ignored) {}
        try { borrowingManager.loadFromFile("borrow.dat"); } catch (Exception ignored) {}
        try { supplierManager.loadFromFile("suppliers.dat"); } catch (Exception ignored) {}
        try { orderManager.loadFromFile("orders.dat"); } catch (Exception ignored) {}
    }

    private void saveAll() {
        bookManager.saveToFile("books.dat");
        userManager.saveToFile("users.dat");
        borrowingManager.saveToFile("borrow.dat");
        supplierManager.saveToFile("suppliers.dat");
        orderManager.saveToFile("orders.dat");
    }

    // ------------------------ FILE EXPLORER / IMPORT ------------------------
    private void openDataFolder() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Open Data Folder");
        File initial = new File(System.getProperty("user.dir"));
        if (initial.exists()) dc.setInitialDirectory(initial);
        File dir = dc.showDialog(primaryStage);
        if (dir == null) return;

        Dialog<Void> dlg = new Dialog<>();
        dlg.setTitle("Folder: " + dir.getAbsolutePath());
        ListView<String> list = new ListView<>();
        File[] files = dir.listFiles();
        if (files != null) for (File f : files) list.getItems().add(f.getName() + (f.isDirectory() ? " [DIR]" : ""));
        dlg.getDialogPane().setContent(list);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.showAndWait();
    }

    private void importDataFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Data File (binary .dat recommended)");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Data files", "*.dat"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File f = fc.showOpenDialog(primaryStage);
        if (f == null) return;

        ChoiceDialog<String> cd = new ChoiceDialog<>("users", "users", "books", "borrow", "suppliers", "orders");
        cd.setTitle("Import Type");
        cd.setHeaderText("Select which manager should load the file");
        Optional<String> res = cd.showAndWait();
        if (res.isEmpty()) return;
        String type = res.get();

        try {
            switch (type) {
                case "users" -> userManager.loadFromFile(f.getAbsolutePath());
                case "books" -> bookManager.loadFromFile(f.getAbsolutePath());
                case "borrow" -> borrowingManager.loadFromFile(f.getAbsolutePath());
                case "suppliers" -> supplierManager.loadFromFile(f.getAbsolutePath());
                case "orders" -> orderManager.loadFromFile(f.getAbsolutePath());
                default -> { showError("Unknown type."); return; }
            }
            showInfo("Imported", "Data imported into " + type + " manager.");
            refreshAllObservable();
        } catch (Exception ex) {
            showError("Import failed: " + ex.getMessage());
        }
    }

    // ------------------------ SMALL UTILITIES ------------------------
    private String getUserRoleName(User u) {
        if (u instanceof Admin) return "Admin";
        if (u instanceof Librarian) return "Librarian";
        if (u instanceof Borrower) return "Borrower";
        return "User";
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private boolean confirm(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        a.setTitle(title);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }

    private void refreshSuppliersList(ListView<String> lv) {
        lv.getItems().clear();
        supplierManager.listSuppliers().forEach(s -> lv.getItems().add(s.toString()));
    }

    private void refreshOrdersList(ListView<String> list) {
        list.getItems().clear();
        orderManager.listOrders().forEach(o -> list.getItems().add(o.toString()));
    }

    // ------------------------ UI helper & styling ------------------------
    private void styleAuthInput(TextField tf) {
        tf.setPrefWidth(360);
        tf.setPrefHeight(44);
        tf.setStyle("-fx-background-radius: 12; -fx-background-color: rgba(255,255,255,0.9); -fx-padding: 0 12 0 12; -fx-font-size: 14;");
        tf.setEffect(new DropShadow(8, Color.rgb(0,0,0,0.12)));
    }

    private Button makeSidebarButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(200);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; -fx-font-size: 14; -fx-background-radius: 8;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14;"));
        return b;
    }

    private void animateReplace(StackPane parent, Node newContent) {
        if (parent.getChildren().isEmpty()) { parent.getChildren().add(newContent); return; }
        Node old = parent.getChildren().get(0);

        // animate old out, new in
        TranslateTransition ttOut = new TranslateTransition(Duration.millis(220), old);
        ttOut.setToX(-20);
        ttOut.setToY(0);
        FadeTransition ftOut = new FadeTransition(Duration.millis(220), old);
        ftOut.setToValue(0.0);

        ParallelTransition out = new ParallelTransition(ttOut, ftOut);
        out.setOnFinished(e -> {
            parent.getChildren().remove(old);
            newContent.setOpacity(0);
            newContent.setTranslateX(20);
            parent.getChildren().add(newContent);
            TranslateTransition ttIn = new TranslateTransition(Duration.millis(260), newContent);
            ttIn.setFromX(20);
            ttIn.setToX(0);
            FadeTransition ftIn = new FadeTransition(Duration.millis(260), newContent);
            ftIn.setFromValue(0.0);
            ftIn.setToValue(1.0);
            new ParallelTransition(ttIn, ftIn).play();
        });
        out.play();
    }

    private void animateShake(Node node) {
        TranslateTransition t1 = new TranslateTransition(Duration.millis(40), node);
        t1.setByX(-8);
        TranslateTransition t2 = new TranslateTransition(Duration.millis(40), node);
        t2.setByX(16);
        TranslateTransition t3 = new TranslateTransition(Duration.millis(40), node);
        t3.setByX(-8);
        SequentialTransition seq = new SequentialTransition(t1, t2, t3);
        seq.play();
    }

    private String toRgbString(Color c) {
        return String.format("#%02x%02x%02x",
                (int)(c.getRed()*255),
                (int)(c.getGreen()*255),
                (int)(c.getBlue()*255));
    }

    // ------------------------ small adapters (for compatibility) ------------------------
    // If your managers expose slightly different method names (e.g., getBooks vs listBooks), update here.

    // ------------------------ MAIN ------------------------
    public static void main(String[] args) {
        launch(args);
    }
}