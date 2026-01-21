
package com.demo.order;
import org.springframework.kafka.core.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/orders")
public class OrderController {
 private final KafkaTemplate<String,String> kafka;
 public OrderController(KafkaTemplate<String,String> kafka){this.kafka=kafka;}
 @PostMapping("/{productId}")
 public String order(@PathVariable String productId){
  kafka.send("order-created", productId);
  return "ORDER_CREATED";
 }
}
