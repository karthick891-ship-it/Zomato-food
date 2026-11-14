package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Getter @Setter @NoArgsConstructor
public class FoodOrder {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne private AppUser user;
  @ManyToOne private Restaurant restaurant;
  private String customerName, phone;
  @Column(length=500) private String deliveryAddress;
  private BigDecimal subtotal, tax, deliveryFee, discount, totalAmount;
  private String couponCode, paymentMethod, paymentStatus;
  private String status = "PLACED";
  private LocalDateTime createdAt = LocalDateTime.now();
  @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true) private List<OrderItem> items = new ArrayList<>();
}
