
package com.demo.inventory;
import jakarta.persistence.*;
@Entity
public class Inventory {
 @Id
 private Long productId;
 private int qty;
 public Long getProductId(){return productId;}
 public void setProductId(Long p){this.productId=p;}
 public int getQty(){return qty;}
 public void setQty(int q){this.qty=q;}
}
