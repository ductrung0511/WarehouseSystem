package com.warehouse.models;

import java.util.Date;

public class Product 
{

    private int productId;
    private String name;
    private String category;
    private int supplierId;
    private Date expiryDate;
    private double price;
    private int quantity;
            
    public Product(int pId, String n, String cat, int q, double price, Date eD, int sId){
        this.productId = pId;
        this.name = n;
        this.category = cat;
        this.quantity = q;
        this.expiryDate = eD;
        this.supplierId = sId;
        this.price = price;
    }
    
    // Getters Fucntions;
    public int getProductId () {return productId;}
    public String getName () {return name;}
    public String getCategory() {return category; }
    public int getQuantity() {return quantity;}
    public double getPrice() {return price;}
    public int getSupplierId(){return supplierId;}
    public Date getExpiryDate() {return expiryDate;}
    
    // Setter's Functtions 
    public void setName(String n) {this.name = n;}
    public void setQuantity(int q) {this.quantity = q;}
    public void setCategory(String cat) {this.category = cat;}
    public void  setPrice(double p) {this.price = p; }
    public  void setSupplierId( int sId) {this.supplierId = sId;}
    public void setProductId( int id) {this.productId = id;}
    
  public void updateStock( int amt) {
    this.quantity  = amt;
  }
  
  public void updatePrice( double newPrice) {
      this.price =  newPrice;
  }
  
//  public void markAsExpired  Not needed 
  
  
  @Override // why override ?
  public String toString() {
    return String.format("Product id = %d", productId );
  }


}