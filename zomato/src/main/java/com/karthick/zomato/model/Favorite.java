package com.karthick.zomato.model;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"user_id","restaurant_id"}))
public class Favorite {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne private AppUser user;
  @ManyToOne private Restaurant restaurant;
}
