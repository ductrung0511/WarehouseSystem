// GPTZero  = 92% Human - Checked
package com.warehouse.models;


    public class Customer extends Person {

    
    public Customer(int personId,String name,String contactInfo,String address) {super(personId, name, contactInfo, address);}

    //  abstract Method from Super class Person
    @Override
    public void updateInfo() {
        System.out.println("Upduted Costumer's info - name = %s " + name);
        
    }


    @Override
public String toString(){
       return  String.format("CID %d - %s", personId, name);
    }
}
