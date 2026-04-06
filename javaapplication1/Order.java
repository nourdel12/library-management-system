package javaapplication1;

import java.io.Serializable;
import java.util.Date;

public class Order implements Validatable, Serializable {

    private static final long serialVersionUID = 1L;

    private int orderId;
    private Supplier supplier;
    private Book book;
    private int quantity;
    private Date orderDate;
    private Date arrivalDate;
    private String status;

    public Order() {}

    public Order(int orderId, Supplier supplier, Book book,
                 int quantity, Date orderDate, String status) {

        this.orderId = orderId;
        this.supplier = supplier;
        this.book = book;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters & Setters
    public int getOrderId() { return orderId; }

    public Supplier getSupplier() { return supplier; }

    public Book getBook() { return book; }

    public int getQuantity() { return quantity; }

    public Date getOrderDate() { return orderDate; }

    public Date getArrivalDate() { return arrivalDate; }

    public String getStatus() { return status; }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        return orderId > 0 &&
               supplier != null &&
               supplier.validate() &&
               book != null &&
               quantity > 0 &&
               orderDate != null &&
               status != null && !status.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Order ID: " + orderId +
               ", Supplier: " + supplier.getName() +
               ", Book: " + book.getTitle() +
               ", Qty: " + quantity +
               ", Status: " + status +
               ", Order Date: " + orderDate +
               ", Arrival: " + arrivalDate;
    }
}

