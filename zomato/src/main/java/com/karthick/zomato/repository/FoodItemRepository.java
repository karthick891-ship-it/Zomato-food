package com.karthick.zomato.repository;
import com.karthick.zomato.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {
  List<FoodItem> findByRestaurantIdAndAvailableTrue(Long restaurantId);
  List<FoodItem> findByRestaurantId(Long restaurantId);
  List<FoodItem> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
}
