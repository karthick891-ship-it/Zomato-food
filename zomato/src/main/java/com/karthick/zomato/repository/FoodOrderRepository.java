package com.karthick.zomato.repository;
import com.karthick.zomato.model.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface FoodOrderRepository extends JpaRepository<FoodOrder,Long> {
  List<FoodOrder> findByUserEmailOrderByCreatedAtDesc(String email);
  List<FoodOrder> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
  List<FoodOrder> findAllByOrderByCreatedAtDesc();
}
