package com.warehouse.models;
        
import java.util.ArrayList;
import java.util.List;


public class Warehouse {
    
private int warehouseId;
private String location;
        //ds 
private String name;
private int capacity;
//sdf
private int currentStock; // realtime
private List<Product> products; 

public Warehouse(int Id, String na, String lo, int ca) {
this.warehouseId = Id;
this.name = na;
// jfkdsljk
this.location = lo;
this.capacity = ca;
this.currentStock = 0; // no at first;
this.products = new ArrayList<>();

}

public int getWarehouseId() {return warehouseId;}
public String getName() {return name;}
public String getLocation() { return this.location;}
public int getCapacity() {return capacity;} 
public int getCurrentStock () {
    return currentStock;
    
}
    
public List<Product> getProducts() {
return products; 
}

public void addProduct (Product pr, int qu) {
    if(currentStock + qu <= capacity) {
        boolean in = false;
        for(Product p : products ) {
            if(p.getProductId() == pr.getProductId()){
                p.updateStock(p.getQuantity() + qu);
                in = true;
                break;
            }}
        if(!in) {
            products.add(pr);
        }
        this.currentStock += qu;
    }

}

public void removeProduct(int pId, int qu){
    for(Product p : products) {
        if(p.getProductId() == pId) {
            if(p.getQuantity() >= qu) {
                p.updateStock(-qu);
                this.currentStock -= qu;
                if(p.getQuantity() == 0 ) products.remove(p);
            }
            else System.out.print(" Not enough stock ... :( ");
            return;
        
        }
        // If not found by ALG
        System.out.print(" P no Found");
    }
        
        
        
        }


public int checkAvailability(int pId) {
  for (Product p : products){
    if(p.getProductId()== pId) {
        return p.getQuantity();
    }
  }
  // if not then return zeno
  return 0;
}
public void setWarehouseId(int id) {this.warehouseId = id;}
public void setCurrentStock(int s) {this.currentStock = s;}
public void generateReport() {
// Not needed, only when return data is needed
}

@Override
public String  toString () 
{
return "WID %d " + warehouseId  + " " + name; // done
}






}