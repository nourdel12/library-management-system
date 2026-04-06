/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.io.Serializable;

public class Supplier implements Validatable, Serializable {

    private static final long serialVersionUID = 1L;

    private int supplierId;
    private String name;
    private String contactInfo;

    // Constructors
    public Supplier() {}

    public Supplier(int supplierId, String name, String contactInfo) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    // Getters & Setters
    public int getSupplierId() { return supplierId; }

    public void setSupplierId(int supplierId) {
        if (supplierId > 0) this.supplierId = supplierId;
    }

    public String getName() { return name; }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty())
            this.name = name;
    }

    public String getContactInfo() { return contactInfo; }

    public void setContactInfo(String contactInfo) {
        if (contactInfo != null && !contactInfo.trim().isEmpty())
            this.contactInfo = contactInfo;
    }

    // Validation
    @Override
    public boolean validate() {
        return supplierId > 0 &&
               name != null && !name.trim().isEmpty() &&
               contactInfo != null && !contactInfo.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Supplier ID: " + supplierId +
               ", Name: " + name +
               ", Contact: " + contactInfo;
    }
}

