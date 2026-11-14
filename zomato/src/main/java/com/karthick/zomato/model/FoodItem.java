package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Getter @Setter @NoArgsConstructor
public class FoodItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(length=500) private String description;
  @Column(nullable=false) private BigDecimal price;
  private String category;
  @Column(length=500) private String imageUrl;
  private boolean veg = true;
  private boolean available = true;
  @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="restaurant_id") private Restaurant restaurant;
}
