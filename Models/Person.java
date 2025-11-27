package com.warehouse.models;
        
public abstract class Person {

protected int personId;
protected String name;
protected String contactInfo;
protected String address;

public Person(int pId, String name, String info, String add){
    this.personId = pId;
    this.name = name;
    this.contactInfo = info;
    this.address = add;
}


// Getter fucntions 
public int getPersonId   () { return this.personId;}
public String getName() {return name ;}
public String getContactInfo() { 
    return contactInfo;}

public String getAddress() {return address;}


// Setters functions
public void setPersonId(int pId) {this.personId = pId;}
public void setname(String n) {this.name = n;}
public void setContactInfo(String inf) {this.contactInfo = inf;}
public void setAdress(String add) { this.address = add; }

//Abstract methid
public abstract void updateInfo();

@Override
public String toString() {
    return String.format( "Person's id = %d", personId );}
}