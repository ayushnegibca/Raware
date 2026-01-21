
package com.demo.inventory;
import org.springframework.kafka.annotation.*;
import org.springframework.stereotype.*;
@Component
public class InventoryConsumer {
 @KafkaListener(topics="order-created")
 public void consume(String productId){
  System.out.println("Reserving stock for product " + productId);
 }
}
