package com.warehouse.models;
import java.util.Date;

public class Shipment {
private int shipmentId;
private int orderId;
private Date deliveryDate;
private String  carrier;
private  String status;


public Shipment(int sId, int oId, Date delDate, String car) {
    this.shipmentId = sId;
    this.orderId = oId;
    this.deliveryDate = delDate;
    this.carrier = car;
    this.status = "Peding"; // shipping, ... more
    
}

// getter's fn
public int getShipmentId() {return shipmentId;}
public int getOrderId() {return orderId;}
public Date getDeliveryDate() { return deliveryDate; }
public String getStatus () {return this.status;}
public String  getCarrier() {return carrier; } // mean of shipment...

public void schedultDelivery(Date d) {
    this.deliveryDate = d;
    }
public void updateStatus(String sta) {
    this.status = sta;
}

public String trackShipment () {
    return "Shipmentt # " + shipmentId;}

@Override
public String toString () { return "Shipemtt id=" + shipmentId;} 

 }