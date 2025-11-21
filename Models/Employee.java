package com.warehouse.models;

public class Employee extends Person {
    private String position;
    private String shift;
    private double salary;

    public Employee(int personId, String name, String contactInfo, String address, 
                   String position, String shift, double salary) {
        super(personId, name, contactInfo, address);
        this.position = position;
        this.shift = shift;
        this.salary = salary;
    }

    // Getters and Setters
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    // Implement abstract method
    @Override
    public void updateInfo() {
        System.out.println("Employee info updated: " + name);
    }

    // Additional methods from UML
    public void clockIn() {
        System.out.println("Employee " + name + " clocked in for " + shift + " shift.");
    }

    public void clockOut() {
        System.out.println("Employee " + name + " clocked out.");
    }

    public void handleOrder(int orderId) {
        System.out.println("Employee " + name + " is handling order #" + orderId);
    }

    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', position='%s', shift='%s', salary=%.2f}", 
                           personId, name, position, shift, salary);
    }
}