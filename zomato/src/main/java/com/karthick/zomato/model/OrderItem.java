package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Getter @Setter @NoArgsConstructor
public class OrderItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) private FoodOrder order;
  private String foodName; private BigDecimal price; private int quantity;
  public BigDecimal getLineTotal(){ return price.multiply(BigDecimal.valueOf(quantity)); }
}
