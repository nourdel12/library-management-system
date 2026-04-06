/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;
import java.io.*;
import java.util.*;


public class BookManager implements FilePersistable {
    private List<Book> books = new ArrayList<>();
    
          // ---------------------- add ----------------------
    public boolean addBook(Book book)
    {
        if (book == null || !book.validate()) 
        {
            System.out.println("Invalid book. Cannot add.");
            return false;
        }

        // Prevent duplicate IDs
        if (searchBook(book.getBookId()) != null) 
        {
            System.out.println("Book ID already exists.");
            return false;
        }

        books.add(book);
        return true;
    }
        // ---------------------- search by id ----------------------

        public Book searchBook(int bookId) {
        for (Book b : books) {
            if (b.getBookId() == bookId) {
                return b;
            }
        }
        return null;
    }
                // ---------------------- search by title ----------------------

        public List<Book> searchByTitle(String title) {
    List<Book> results = new ArrayList<>();
    for (Book b : books) {
        if (b.getTitle().toLowerCase().contains(title.toLowerCase())) {
            results.add(b);
        }
    }
    return results;
}

        // ---------------------- edit ----------------------

     public boolean editBook(int bookId, Book updatedBook) {
        Book existing = searchBook(bookId);

        if (existing == null) {
            System.out.println("Book not found.");
            return false;
        }

        if (updatedBook == null || !updatedBook.validate()) {
            System.out.println("Updated book is invalid.");
            return false;
        }

        // Update fields
        existing.setTitle(updatedBook.getTitle());
        existing.setCategory(updatedBook.getCategory());
        existing.setQuantity(updatedBook.getQuantity());
        existing.setRating(updatedBook.getRating());
        existing.setTotalBorrowCount(updatedBook.getTotalBorrowCount());

        return true;
    }

     // ---------------------- remove ----------------------
    public boolean removeBook(int bookId) 
    {
        Book book = searchBook(bookId);
        if (book != null) {
            books.remove(book);
            return true;
        }
        return false;
    }
    
    // ---------------------- list ----------------------
    public List<Book> listBooks() 
    {
        return books;
    }
        // ---------------------- SAVE ----------------------
          @Override
public void saveToFile(String filename) 
{
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {

        out.writeObject(books);   // <-- Writes the entire ArrayList<Book>

    } catch (IOException e) {
        System.out.println("Error saving to file: " + e.getMessage());
    }
}
           // ---------------------- LOAD ----------------------

@Override
@SuppressWarnings("unchecked")
public void loadFromFile(String filename) {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {

        books = (List<Book>) in.readObject();   // <-- Reads the full list

        // optional: validate each book
        books.removeIf(b -> !b.validate());

    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error loading from file: " + e.getMessage());
    }
}

        public List<Book> getBooks() {
    return new ArrayList<>(books); // safe copy
}

        public boolean isEmpty() {
    return books.isEmpty();
}

        public void clear() {
    books.clear();
}
        
public int countBooks() {
    return books.size();
}

public void displayBooks() {
    for (Book b : books) {
        System.out.println(b);
    }
}

public void sortByTitle() {
    books.sort(Comparator.comparing(Book::getTitle));
}

public boolean exists(int id) {
    return searchBook(id) != null;
}


    
}
