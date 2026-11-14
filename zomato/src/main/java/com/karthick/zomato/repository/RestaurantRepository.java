package com.karthick.zomato.repository;
import com.karthick.zomato.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
  List<Restaurant> findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCaseOrLocationContainingIgnoreCase(String n,String c,String l);
  Optional<Restaurant> findFirstByOwnerEmail(String email);
}
