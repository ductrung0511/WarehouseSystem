package com.warehouse.models;

import java.util.Date;

public class SystemAdmin extends Person {
    private String role;
    private Date lastLogin;

    public SystemAdmin(int personId, String name, String contactInfo, String address, 
                      String role, Date lastLogin) {
        super(personId, name, contactInfo, address);
        this.role = role;
        this.lastLogin = lastLogin;
    }

    // Getters and Setters
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Date getLastLogin() { return lastLogin; }
    public void setLastLogin(Date lastLogin) { this.lastLogin = lastLogin; }

    // Implement abstract method
    @Override
    public void updateInfo() {
        System.out.println("SystemAdmin info updated: " + name);
    }

    // Additional methods from UML
    public void addUser(Person person) {
        System.out.println("Admin " + name + " added user: " + person.getName());
    }

    public void removeUser(int userId) {
        System.out.println("Admin " + name + " removed user ID: " + userId);
    }

    public void updatePermissions(int userId, String role) {
        System.out.println("Admin " + name + " updated permissions for user " + userId + " to role: " + role);
    }

    public void viewLogs() {
        System.out.println("Admin " + name + " is viewing system logs");
    }

    @Override
    public String toString() {
        return String.format("SystemAdmin{id=%d, name='%s', role='%s', lastLogin=%s}", 
                           personId, name, role, lastLogin);
    }
}