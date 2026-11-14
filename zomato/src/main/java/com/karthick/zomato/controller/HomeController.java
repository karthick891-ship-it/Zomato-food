package com.karthick.zomato.controller;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
@Controller @RequiredArgsConstructor
public class HomeController {
  private final RestaurantRepository restaurants; private final FoodItemRepository foods;
  private final ReviewRepository reviews; private final FavoriteRepository favs; private final UserRepository users;
  static final List<String> CATEGORIES=List.of("Biryani","Pizza","Burgers","South Indian","Chinese","Desserts");
  @GetMapping("/")
  public String home(@RequestParam(required=false) String q,@RequestParam(required=false) String category,Model m){
    List<Restaurant> list=(q==null||q.isBlank())?restaurants.findAll()
      :restaurants.findByNameContainingIgnoreCaseOrCuisineContainingIgnoreCaseOrLocationContainingIgnoreCase(q,q,q);
    if(category!=null&&!category.isBlank()) list=list.stream().filter(r->r.getCuisine()!=null&&r.getCuisine().toLowerCase().contains(category.toLowerCase())).toList();
    m.addAttribute("restaurants",list); m.addAttribute("q",q); m.addAttribute("category",category); m.addAttribute("categories",CATEGORIES);
    m.addAttribute("title","Popular restaurants");
    return "index";
  }
  @GetMapping("/favorites")
  public String favorites(Principal p,Model m){
    m.addAttribute("restaurants",favs.findByUserEmail(p.getName()).stream().map(Favorite::getRestaurant).toList());
    m.addAttribute("categories",List.of()); m.addAttribute("title","Your favourite restaurants");
    return "index";
  }
  @GetMapping("/restaurant/{id}")
  public String restaurant(@PathVariable Long id,@RequestParam(required=false) String q,@RequestParam(defaultValue="false") boolean veg,Principal p,Model m){
    var r=restaurants.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    var list=foods.findByRestaurantIdAndAvailableTrue(id).stream()
      .filter(f->q==null||q.isBlank()||f.getName().toLowerCase().contains(q.toLowerCase()))
      .filter(f->!veg||f.isVeg()).toList();
    m.addAttribute("restaurant",r); m.addAttribute("foods",list); m.addAttribute("q",q); m.addAttribute("veg",veg);
    m.addAttribute("reviews",reviews.findByRestaurantIdOrderByCreatedAtDesc(id));
    m.addAttribute("isFav",p!=null&&favs.findByUserEmailAndRestaurantId(p.getName(),id).isPresent());
    return "restaurant";
  }
  @PostMapping("/restaurant/{id}/review")
  public String review(@PathVariable Long id,@RequestParam int rating,@RequestParam(required=false) String comment,Principal p){
    var r=restaurants.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    var rv=new Review(); rv.setUser(users.findByEmail(p.getName()).orElseThrow()); rv.setRestaurant(r);
    rv.setRating(Math.max(1,Math.min(5,rating))); rv.setComment(comment); reviews.save(rv);
    Double avg=reviews.avgRating(id); if(avg!=null){ r.setRating(Math.round(avg*10)/10.0); restaurants.save(r); }
    return "redirect:/restaurant/"+id;
  }
  @PostMapping("/favorite/{id}")
  public String toggleFav(@PathVariable Long id,Principal p){
    var ex=favs.findByUserEmailAndRestaurantId(p.getName(),id);
    if(ex.isPresent()) favs.delete(ex.get());
    else { var f=new Favorite(); f.setUser(users.findByEmail(p.getName()).orElseThrow());
      f.setRestaurant(restaurants.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND))); favs.save(f); }
    return "redirect:/restaurant/"+id;
  }
}
