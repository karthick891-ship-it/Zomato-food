package com.karthick.zomato.repository;
import com.karthick.zomato.model.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review,Long> {
  List<Review> findByRestaurantIdOrderByCreatedAtDesc(Long id);
  @Query("select avg(r.rating) from Review r where r.restaurant.id=:id") Double avgRating(@Param("id") Long id);
}
