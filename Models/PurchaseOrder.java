package  com.warehouse.models;
import java.util.*;

public class  PurchaseOrder  
{
    private int poId;
    private int  supplierId; // Diff from Orders
    
    private Map<Product ,Integer>  productList ;
    private double totalCost;
    private Date orderDate;
    private String  status;
    
    public PurchaseOrder(int id, int sId, Date d){
        this.poId = id;
    this.supplierId = sId;
    this.orderDate = d;
    this.productList = new HashMap<>();
    this.totalCost = 0.0;
    this.status = "Draft";
        System.out.println("Created new Purchase Order! - PoId: " + poId);
    }
    
    // Funcstions Getters --- 
    public int  getPoId() {return poId; } // as poId is private
    public int getSupplierId() {return supplierId;}
    public Map<Product, Integer> getProductList() {return productList;}
    public double  getTotalCost () { return totalCost;}
    public Date getOrderDate() {return orderDate; }
    public String getStatus() {return status;}
    
    public void cancelPO() { this.status = "Cancelled"; 
    } // Do st else here ... ?
    
    public void updateStatus(String sta) {
        this.status = sta;
    }
    
    public void addProduct (Product p, int q) {
    productList.put(p, productList.getOrDefault(p, 0) + q );
    this.totalCost += p.getPrice() * q;
            }
    
    @Override 
    public  String  toString() 
    {
    
    return String.format("POid #%d, Sid #%d", poId, supplierId);}

}