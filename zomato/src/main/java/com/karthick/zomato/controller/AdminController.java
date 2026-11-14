package com.karthick.zomato.controller;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Controller @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {
  private final UserRepository users; private final RestaurantRepository restaurants; private final FoodOrderRepository orders; private final CouponRepository coupons;
  @GetMapping
  public String dashboard(Model m){
    var all=orders.findAllByOrderByCreatedAtDesc();
    var byStatus=all.stream().collect(Collectors.groupingBy(FoodOrder::getStatus,TreeMap::new,Collectors.counting()));
    long max=byStatus.values().stream().mapToLong(Long::longValue).max().orElse(1);
    m.addAttribute("userCount",users.count()); m.addAttribute("restaurantCount",restaurants.count()); m.addAttribute("orderCount",all.size());
    m.addAttribute("revenue",all.stream().filter(o->"DELIVERED".equals(o.getStatus())).map(FoodOrder::getTotalAmount).reduce(BigDecimal.ZERO,BigDecimal::add));
    m.addAttribute("byStatus",byStatus); m.addAttribute("maxCount",max);
    m.addAttribute("users",users.findAll()); m.addAttribute("restaurants",restaurants.findAll()); m.addAttribute("orders",all); m.addAttribute("coupons",coupons.findAll());
    return "admin/dashboard";
  }
  @PostMapping("/coupon")
  public String addCoupon(@RequestParam String code,@RequestParam int percent,@RequestParam BigDecimal maxDiscount,@RequestParam BigDecimal minOrder){
    if(!code.isBlank()&&percent>0&&percent<=100&&coupons.findByCodeIgnoreCaseAndActiveTrue(code).isEmpty()){
      var c=new Coupon(); c.setCode(code.trim().toUpperCase()); c.setPercent(percent); c.setMaxDiscount(maxDiscount); c.setMinOrder(minOrder);
      try{ coupons.save(c);}catch(Exception ignored){} }
    return "redirect:/admin";
  }
  @PostMapping("/coupon/{id}/toggle")
  public String toggle(@PathVariable Long id){ coupons.findById(id).ifPresent(c->{ c.setActive(!c.isActive()); coupons.save(c); }); return "redirect:/admin"; }
}
