/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.io.Serializable;
public class Category implements Serializable , Validatable {
    
    private static final long serialVersionUID = 1L;
    private int categoryId;
    private String name;
    
        // ---------- Constructors ----------
    public Category() {}

    public Category(int categoryId, String name) 
    {
        this.categoryId = categoryId;
        this.name = name;
    }
     // ---------- Getters & Setters ----------
    public int getCategoryId() 
    {
        return categoryId;
    }

    public void setCategoryId(int categoryId) 
    {
        if (categoryId > 0) {
            this.categoryId = categoryId;
        }
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) 
        {
            this.name = name;
        }
    }
      // ---------- Validation ----------
    @Override
    public boolean validate() {
        if (categoryId <= 0)
            return false;

        if (name == null || name.trim().isEmpty())
            return false;

        return true;
    }
    // ---------- toString ----------
    @Override
    public String toString() {
        return "Category ID: " + categoryId + ", Name: " + name;
    }
        // ---------- equals & hashCode ----------
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Category)) return false;

        Category other = (Category) obj;
        return this.categoryId == other.categoryId;
    }
        @Override
    public int hashCode() {
        return Integer.hashCode(categoryId);
    }
    

}
