package com.karthick.zomato.repository;
import com.karthick.zomato.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
  List<Favorite> findByUserEmail(String email);
  Optional<Favorite> findByUserEmailAndRestaurantId(String email, Long restaurantId);
}
