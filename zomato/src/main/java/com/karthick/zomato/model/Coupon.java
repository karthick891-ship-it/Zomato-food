package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
@Entity @Getter @Setter @NoArgsConstructor
public class Coupon {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(unique=true,nullable=false) private String code;
  private int percent; private BigDecimal maxDiscount = BigDecimal.valueOf(1000), minOrder = BigDecimal.ZERO;
  private boolean active = true;
}
