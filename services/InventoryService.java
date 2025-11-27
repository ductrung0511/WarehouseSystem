package com.warehouse.services
        ;

import com.warehouse.dao.ProductDAO;
import com.warehouse.models.Product;
import java.util.*;

public class InventoryService {
    private  ProductDAO productDAO;
    
    public InventoryService(){
    
    this.productDAO = new ProductDAO();
    }
    
    public List<Product> getLowStockProducts(int th){
    
            List <Product> allP = productDAO.getAllProducts();
            List <Product> lowP = new ArrayList<>();

            for(Product p: allP){
                if(p.getQuantity() <= th){
                    lowP.add(p);
                }
            }
            return lowP;
    }
    
    
    public List<Product> getOutOfStockProducts (){ return getLowStockProducts(0);}
    // just call above function witgh th = 0 ->
    
    public boolean restockProduct(int pId, int q){
        Product p = productDAO.getProductById(pId);
        if(p!=null) {return productDAO.updateProductStock(pId, p.getQuantity()+ q);
        }
        return false;
    
    }
    
    public double getTotalInventoryValue() {
        List<Product> ps = productDAO.getAllProducts();
        double t = 0.0;
        for (Product p: ps){
            t += p.getPrice() * p.getQuantity();
            
        
        }
        return t;
}
    
    public String getInventorySummary() {
        List<Product> ps = productDAO.getAllProducts();
        int totalP = ps.size();
        int tItems  = 0;
        double tValue = 0.0;
        int lowStockCount = 0;
        
        for (Product p: ps) {
            tItems += p.getQuantity();
            tValue += p.getPrice() * p.getQuantity();
            if(p.getQuantity() < 10 ){ // replace by input variable if needed
                lowStockCount ++;
            }
        }
        return String.format("Inventory Summary : " + "total Products: %d\n" + "total Items: %d\n " + "total value: $%.2f\n" 
                + "Low Stock Items : %d", totalP, tItems, tValue, lowStockCount );
        
    }
    

}