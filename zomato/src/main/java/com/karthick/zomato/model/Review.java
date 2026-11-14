package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity @Getter @Setter @NoArgsConstructor
public class Review {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne private AppUser user;
  @ManyToOne private Restaurant restaurant;
  private int rating; @Column(length=500) private String comment;
  private LocalDateTime createdAt = LocalDateTime.now();
}
