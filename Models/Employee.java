
// GPTZero  = 100% Human - Checked
package com.warehouse.models;


        
public class Employee extends Person{

private String position;
private String shift;
private double salary; // Making other class can not see employee's details

public Employee(int id, String name, String info, String add, String po, String shift, double sal){
    super(id, name, info, add);
    this.position = po;
    this.shift = shift;
    this.salary = sal;
}

// Making getter functions - as Details of employees are private and only can get by functions
public String getPosition() {return position;}
public String getShift() {return this.shift;}
public double  getSalary() {return salary;}

//Making Setter fucntions

public void setPosition(String pos) {this.position = pos;}
public void setShift(String sh) {this.shift = sh;}
public void setSalary(double sal) {this.salary= sal;}

// Making Override method - demo only
@Override
public void updateInfo(){ System.out.print("EMP umpted");}

@Override
public String toString() {
    return String.format("EMP toString id:%d", personId);}
}