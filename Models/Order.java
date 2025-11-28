package com.warehouse.models;
import java.util.*; // what for ?

public class Order {

    private int orderId;
    private int customerId;
    private Map<Product, Integer> productList; // An Order has Product with varable amount --
    private double totalAmount;
    private String status; // Peding, Procesing, Shiped, Dileverd 
    private Date date; // date of creation

    public Order(int id, int cusId, Date date){
        this.orderId = id;
        this.customerId = cusId;
        this.date = date;
        this.productList = new HashMap<>();
        this.totalAmount = 0.0; // nothing first
       this.status = "Pending";
    }
    
    public int getOrderId() {return orderId;}
    public int getCustomerId() {return this.customerId;}
    public Map<Product, Integer> getProductList() {return this.productList;}
    public double getTotalAmount() {return totalAmount;}
    public String getStatus() {return this.status;}
    public Date getDate() {return date;}
    
    public void setOrderId(int id) {this.orderId = id;}
    public void addItem(Product p, int q){
        productList.put( p , productList.getOrDefault(p ,0) + q);
        // if Product is included in the proDuctList then take product quantity 
        // else not in list tben 0
        this.totalAmount += p.getPrice() * q;
        System.out.print("Added product " + p.getName() + " - quantity: " + q + " to order's id: " + orderId);
    }
    
    public void removeItem(int pId){
        Product p = null;
        for(Map.Entry<Product, Integer> item : productList.entrySet()){ // loop through every item in List
            if(item.getKey().getProductId() == pId) { // if itemId = Pid then remove
                p = item.getKey(); // get the product object 
                totalAmount -= p.getPrice() * item.getValue(); // subtract from total order
            }
        }
        if(p != null){
            productList.remove(p); // actaul remove
            System.out.print("Rmv p Id " + pId + " - order# " + orderId);
        }
      }
    
    public void processOrder(){
        this.status = "Processed"; 
        // Do some notification here to related actorss
        System.out.println("Proccesing OrderID = " + orderId);
    }
    
    public void cancelOrder() {
        this.status = "Canceled";
                // Do some notification here to related actorss
        System.out.println("Cancelling orderr#" + orderId );
    }
    
    public void updateStatus(String st) {
        this.status = st;
        System.out.println("Order #" +orderId+ "changged to status of: " + status);
    }
    
    @Override 
    
        public String  toString() 
     {
        return String.format("OrderId #%d, CusId #%d, total = %.2f, %s, %d items", orderId, customerId, totalAmount, status, productList.size());
    }

    
    
    
    


       
        
        
        
        
        }