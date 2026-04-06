package javaapplication1;


import java.io.*;
import java.util.*;

public class OrderManager implements FilePersistable {

    private List<Order> orders = new ArrayList<>();

    public boolean placeOrder(Order order) {
        if (order == null || !order.validate()) return false;
        if (search(order.getOrderId()) != null) return false;

        orders.add(order);
        return true;
    }

    public Order search(int orderId) {
        for (Order o : orders)
            if (o.getOrderId() == orderId)
                return o;
        return null;
    }

    public boolean receiveOrder(int orderId, Date arrivalDate) {
        Order o = search(orderId);
        if (o == null) return false;

        o.setArrivalDate(arrivalDate);
        o.setStatus("Delivered");

        // Increase book quantity
        Book book = o.getBook();
        if (book != null) {
            book.setQuantity(book.getQuantity() + o.getQuantity());
        }

        return true;
    }

    public boolean cancelOrder(int orderId) {
        Order o = search(orderId);
        if (o != null) {
            o.setStatus("Cancelled");
            return true;
        }
        return false;
    }

    public List<Order> listOrders() { return orders; }

    @Override
    public void saveToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(orders);
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            orders = (List<Order>) in.readObject();
            orders.removeIf(o -> !o.validate());
        } catch (Exception e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
    }
}
