package com.karthick.zomato.controller;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
@Controller @RequestMapping("/owner") @RequiredArgsConstructor
public class OwnerController {
  private final RestaurantRepository restaurants; private final FoodItemRepository foods; private final FoodOrderRepository orders;
  static final List<String> NEXT=List.of("ACCEPTED","REJECTED","PREPARING","OUT_FOR_DELIVERY","DELIVERED");
  private Restaurant mine(Principal p){ return restaurants.findFirstByOwnerEmail(p.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"No restaurant")); }
  @GetMapping
  public String dashboard(Principal p,Model m){
    var r=mine(p); var os=orders.findByRestaurantIdOrderByCreatedAtDesc(r.getId());
    BigDecimal sales=os.stream().filter(o->"DELIVERED".equals(o.getStatus())).map(FoodOrder::getTotalAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
    m.addAttribute("restaurant",r); m.addAttribute("foods",foods.findByRestaurantId(r.getId())); m.addAttribute("orders",os);
    m.addAttribute("sales",sales); m.addAttribute("delivered",os.stream().filter(o->"DELIVERED".equals(o.getStatus())).count());
    m.addAttribute("pending",os.stream().filter(o->"PLACED".equals(o.getStatus())).count()); m.addAttribute("next",NEXT);
    return "owner/dashboard";
  }
  @GetMapping("/food/new") public String newFood(Principal p,Model m){ mine(p); m.addAttribute("food",new FoodItem()); return "owner/food-form"; }
  @GetMapping("/food/{id}/edit")
  public String edit(@PathVariable Long id,Principal p,Model m){ m.addAttribute("food",owned(id,p)); return "owner/food-form"; }
  private FoodItem owned(Long id,Principal p){
    var f=foods.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    if(!f.getRestaurant().getId().equals(mine(p).getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN); return f;
  }
  @PostMapping("/food")
  public String save(@RequestParam(required=false) Long id,@RequestParam String name,@RequestParam(required=false) String description,
      @RequestParam BigDecimal price,@RequestParam String category,@RequestParam(required=false) String imageUrl,
      @RequestParam(defaultValue="false") boolean veg,@RequestParam(defaultValue="false") boolean available,Principal p){
    var f=id==null?new FoodItem():owned(id,p); if(id==null) f.setRestaurant(mine(p));
    if(price.signum()<=0||name.isBlank()) return "redirect:/owner";
    f.setName(name); f.setDescription(description); f.setPrice(price); f.setCategory(category);
    f.setImageUrl(imageUrl==null||imageUrl.isBlank()?null:imageUrl); f.setVeg(veg); f.setAvailable(available);
    foods.save(f); return "redirect:/owner";
  }
  @PostMapping("/food/{id}/delete") public String delete(@PathVariable Long id,Principal p){ foods.delete(owned(id,p)); return "redirect:/owner"; }
  @PostMapping("/order/{id}/status")
  public String status(@PathVariable Long id,@RequestParam String status,Principal p){
    var o=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    if(!o.getRestaurant().getId().equals(mine(p).getId())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    if(NEXT.contains(status)&&!List.of("CANCELLED","DELIVERED","REJECTED").contains(o.getStatus())){
      o.setStatus(status); if("DELIVERED".equals(status)&&"PENDING".equals(o.getPaymentStatus())) o.setPaymentStatus("PAID");
      if("REJECTED".equals(status)&&"PAID".equals(o.getPaymentStatus())) o.setPaymentStatus("REFUND_PENDING");
      orders.save(o); }
    return "redirect:/owner";
  }
}
