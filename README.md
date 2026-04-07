#  Library Management System (JavaFX)

A JavaFX-based desktop application for managing library operations including books, users, borrowing, and orders.

---

## Features

```text
- Book CRUD (Add / Edit / Delete)
- User management
- Borrow & return system
- Supplier & order handling
- Dashboard overview
- File-based persistence (.dat)
```

---

##  Tech Stack

```text
Language      : Java
UI Framework  : JavaFX
Concepts      : OOP, Collections
Storage       : File Handling (.dat)
```

---

##  Run the Project

```bash
# Compile
javac --module-path "PATH_TO_FX/lib" \
--add-modules javafx.controls,javafx.fxml *.java

# Run
java --module-path "PATH_TO_FX/lib" \
--add-modules javafx.controls,javafx.fxml \
javaapplication1.LibraryApp
```

---

## Structure

```text
LibraryApp.java        -> Main UI
Book/User/Supplier     -> Models
*Manager.java          -> Business Logic
*.dat                  -> Data storage
```

---

##  Concepts Used

```text
- Encapsulation
- Inheritance
- Modular Design
- Observable Lists (JavaFX)
```
---
##  Authors

Nour Adel
Mawada Osama

