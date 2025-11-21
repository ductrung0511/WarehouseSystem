package com.warehouse.models;

import java.util.Date;

public class InventoryRecord {
    private int recordId;
    private int productId;
    private int quantityIn;
    private int quantityOut;
    private Date date;
    private int updatedBy;

    public InventoryRecord(int recordId, int productId, int quantityIn, int quantityOut, 
                          Date date, int updatedBy) {
        this.recordId = recordId;
        this.productId = productId;
        this.quantityIn = quantityIn;
        this.quantityOut = quantityOut;
        this.date = date;
        this.updatedBy = updatedBy;
    }

    // Getters and Setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantityIn() { return quantityIn; }
    public void setQuantityIn(int quantityIn) { this.quantityIn = quantityIn; }

    public int getQuantityOut() { return quantityOut; }
    public void setQuantityOut(int quantityOut) { this.quantityOut = quantityOut; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public int getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(int updatedBy) { this.updatedBy = updatedBy; }

    // Methods from UML
    public void updateRecord() {
        System.out.println("Inventory record updated for product ID: " + productId);
    }

    public void viewRecord() {
        System.out.println("Viewing inventory record for product ID: " + productId);
    }

    public void generateReport() {
        System.out.println("Generating inventory report for record ID: " + recordId);
    }

    @Override
    public String toString() {
        return String.format("InventoryRecord{id=%d, productId=%d, in=%d, out=%d, date=%s}", 
                           recordId, productId, quantityIn, quantityOut, date);
    }
}