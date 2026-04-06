/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;
import java.io.Serializable;

public class Book implements Validatable ,Serializable  {
        private static final long serialVersionUID = 1L;
    private int bookId;
    private String title;
    private Category category;
    private int quantity;
    private int totalBorrowCount;
    private double rating;
    //constructor
    public Book() {}

public Book(int bookId, String title, Category category, int quantity, 
           int totalBorrowCount, double rating) 
{
        this.bookId = bookId;
        this.title = title;
        this.category = category;
        this.quantity = quantity;
        this.totalBorrowCount = totalBorrowCount;
        this.rating = rating;
}

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }
    

    public Category getCategory() 
    {
        return category;
    }

    public void setCategory(Category category) 
    {
        this.category = category;
    }
    

    public int getQuantity() 
    {
        return quantity;
    }

   public void setQuantity(int quantity)
{
    if (quantity >= 0)
    {
        this.quantity = quantity;
    }
    
}


    public int getTotalBorrowCount() 
    {
        return totalBorrowCount;
    }

    public void setTotalBorrowCount(int totalBorrowCount) 
    {
        this.totalBorrowCount = totalBorrowCount;
    }

    
    public double getRating() 
    {
        return rating;
    }

    public void setRating(double rating) 
    {
        this.rating = rating;
    }
    
    @Override
     public boolean validate() 
     {
            if (bookId <= 0)
            {
                return false;
            }

            if (title == null || title.trim().isEmpty())  //title.trim().isEmpty():Prevents whitespace-only titles
            {
                return false;
            }
            if (category == null)  //Ensures every book belongs to a category (as required in your project).
            {
                return false;
            }

            if (quantity < 0)
            {
                return false;
            }
            if (rating < 0.0 || rating > 5.0)
            {
                return false;
            }

            return true;
     }
     public void incrementBorrowCount() {
    totalBorrowCount++;
}

     public void decreaseQuantity() {
    if (quantity > 0)
        quantity--;
}

public void increaseQuantity() {
    quantity++;
}

@Override
public String toString() {
    return "Book ID: " + bookId +
           ", Title: " + title +
           ", Category: " + category +
           ", Quantity: " + quantity +
           ", Total Borrowed: " + totalBorrowCount +
           ", Rating: " + rating;
}

@Override
public boolean equals(Object obj) 
//Needed for searching, comparing, and storing in collections.
{
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Book other = (Book) obj;
    return this.bookId == other.bookId;
}

@Override
public int hashCode() 
{
    return Integer.hashCode(bookId);
}


}
