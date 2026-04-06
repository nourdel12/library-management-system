/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication1;

import java.io.Serializable;

public abstract class User implements Validatable, Serializable {

    private static final long serialVersionUID = 1L;

    protected int userId;
    protected String name;
    protected String email;
     protected String password;

    // ---------- Constructors ----------
    public User() {}

    public User(int userId, String name, String email,String password ) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password=password;
    }

    // ---------- Getters & Setters ----------
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId > 0)
            this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty())
            this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@"))
            this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    // ---------- LOGIN / LOGOUT ----------
    public void login() {
        System.out.println(name + " has logged in.");
    }

    public void logout() {
        System.out.println(name + " has logged out.");
    }

    // ---------- VALIDATION ----------
    @Override
    public boolean validate() {
        if (userId <= 0) return false;
        if (name == null || name.trim().isEmpty()) return false;
        if (email == null || !email.contains("@")) return false;

        return true;
    }

    // ---------- toString ----------
    @Override
    public String toString() {
        return "User ID: " + userId +
               ", Name: " + name +
               ", Email: " + email;
    }
}
