package com.warehouse.models;

import java.util.Date;

public class Shipment {
    private int shipmentId;
    private int orderId;
    private Date deliveryDate;
    private String carrier;
    private String status;

    public Shipment(int shipmentId, int orderId, Date deliveryDate, String carrier) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.deliveryDate = deliveryDate;
        this.carrier = carrier;
        this.status = "Pending";
    }

    // Getters and Setters
    public int getShipmentId() { return shipmentId; }
    public int getOrderId() { return orderId; }
    public Date getDeliveryDate() { return deliveryDate; }
    public String getCarrier() { return carrier; }
    public String getStatus() { return status; }

    // Methods from UML
    public void scheduleDelivery(Date date) {
        this.deliveryDate = date;
        System.out.println("Shipment #" + shipmentId + " scheduled for: " + date);
    }

    public void updateStatus(String status) {
        this.status = status;
        System.out.println("Shipment #" + shipmentId + " status updated to: " + status);
    }

    public String trackShipment() {
        return "Shipment #" + shipmentId + " - Status: " + status + " - Carrier: " + carrier + 
               " - Estimated Delivery: " + deliveryDate;
    }

    @Override
    public String toString() {
        return String.format("Shipment{id=%d, orderId=%d, carrier='%s', status='%s', delivery=%s}", 
                           shipmentId, orderId, carrier, status, deliveryDate);
    }
}