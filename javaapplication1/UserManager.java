package javaapplication1;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements FilePersistable {

    private List<User> users = new ArrayList<>();

    // --------------------------- ADD USER ---------------------------

    public boolean addUser(User user) {
        if (user == null || !user.validate())
            return false;

        // prevent duplicate IDs
        if (searchUser(user.getUserId()) != null)
            return false;

        users.add(user);
        return true;
    }

    // --------------------------- EDIT USER ---------------------------

    public boolean editUser(int id, User updated) {

        User existing = searchUser(id);

        if (existing == null || updated == null)
            return false;

        // Update only allowed fields
        if (updated.getName() != null)
            existing.setName(updated.getName());

        if (updated.getEmail() != null)
            existing.setEmail(updated.getEmail());

        if (updated.getPassword() != null)
            existing.setPassword(updated.getPassword());

        return true;
    }

    // --------------------------- REMOVE USER ---------------------------

    public boolean removeUser(int id) {
        User u = searchUser(id);
        if (u != null) {
            users.remove(u);
            return true;
        }
        return false;
    }

    // --------------------------- SEARCH BY ID ---------------------------

    public User searchUser(int id) {
        for (User u : users) {
            if (u.getUserId() == id)
                return u;
        }
        return null;
    }

    // --------------------------- SEARCH BY EMAIL + PASSWORD (LOGIN) ---------------------------

    public User login(String email, String password) {

        if (email == null || password == null)
            return null;

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) &&
                u.getPassword().equals(password)) {

                return u;  // valid login
            }
        }

        return null; // failed login
    }

    // --------------------------- LIST ALL USERS ---------------------------

    public List<User> listUsers() {
        return users;
    }

    // --------------------------- SAVE TO FILE ---------------------------

    @Override
    public void saveToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // --------------------------- LOAD FROM FILE ---------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            users = (List<User>) in.readObject();

            // remove corrupted / invalid entries
            users.removeIf(u -> !u.validate());

        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }
        // --------------------------- Display users---------------------------

    public void displayUsers() {
    if (users.isEmpty()) {
        System.out.println("No users found.");
        return;
    }

    for (User u : users) {
        System.out.println(u);
    }
}

}

