package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
public class Restaurant {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  private String cuisine, location, imageUrl;
  private double rating;
  @ManyToOne @JoinColumn(name="owner_id") private AppUser owner;
}
