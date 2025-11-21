package com.warehouse.models;

public abstract class Person {
    protected int personId;
    protected String name;
    protected String contactInfo;
    protected String address;

    public Person(int personId, String name, String contactInfo, String address) {
        this.personId = personId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
    }

    // Getters and Setters
    public int getPersonId() { return personId; }
    public void setPersonId(int personId) { this.personId = personId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Abstract method
    public abstract void updateInfo();

    @Override
    public String toString() {
        return String.format("Person{id=%d, name='%s', contact='%s'}", 
                           personId, name, contactInfo);
    }
}