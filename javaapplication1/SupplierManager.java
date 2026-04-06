/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.io.*;
import java.util.*;

public class SupplierManager implements FilePersistable {

    private List<Supplier> suppliers = new ArrayList<>();

    public boolean addSupplier(Supplier supplier) {
        if (supplier == null || !supplier.validate()) return false;
        if (search(supplier.getSupplierId()) != null) return false;

        suppliers.add(supplier);
        return true;
    }

    public Supplier search(int supplierId) {
        for (Supplier s : suppliers)
            if (s.getSupplierId() == supplierId)
                return s;
        return null;
    }

    public boolean editSupplier(int supplierId, Supplier updated) {
        Supplier existing = search(supplierId);
        if (existing == null || updated == null || !updated.validate()) return false;

        existing.setName(updated.getName());
        existing.setContactInfo(updated.getContactInfo());
        return true;
    }

    public boolean removeSupplier(int supplierId) {
        Supplier s = search(supplierId);
        if (s != null) return suppliers.remove(s);
        return false;
    }

    public List<Supplier> listSuppliers() { return suppliers; }

    @Override
    public void saveToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(suppliers);
        } catch (IOException e) {
            System.out.println("Error saving suppliers: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            suppliers = (List<Supplier>) in.readObject();
            suppliers.removeIf(s -> !s.validate());
        } catch (Exception e) {
            System.out.println("Error loading suppliers: " + e.getMessage());
        }
    }
    //--------------------------------Display suppliers---------------------------------------------
    public void displaySuppliers() {
    if (suppliers.isEmpty()) {
        System.out.println("No suppliers found.");
        return;
    }

    for (Supplier s : suppliers) {
        System.out.println(s);
    }
}

}
