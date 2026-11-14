package com.karthick.zomato.controller;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller @RequiredArgsConstructor
public class AuthController {
  private final UserRepository users; private final RestaurantRepository restaurants; private final PasswordEncoder enc;
  @GetMapping("/login") public String login(){ return "login"; }
  @GetMapping("/register") public String form(){ return "register"; }
  @PostMapping("/register")
  public String register(@RequestParam String name,@RequestParam String email,@RequestParam(required=false) String phone,
      @RequestParam String password,@RequestParam(defaultValue="CUSTOMER") String role,
      @RequestParam(required=false) String restaurantName,@RequestParam(required=false) String cuisine,
      @RequestParam(required=false) String location,Model m){
    String err=null;
    if(name.isBlank()||!email.contains("@")) err="Enter a valid name and email";
    else if(password.length()<6) err="Password must be at least 6 characters";
    else if(users.existsByEmail(email.toLowerCase())) err="Email already registered";
    else if(!role.equals("CUSTOMER")&&!role.equals("RESTAURANT")) err="Invalid account type";
    else if(role.equals("RESTAURANT")&&(restaurantName==null||restaurantName.isBlank())) err="Restaurant name is required";
    if(err!=null){ m.addAttribute("error",err); return "register"; }
    var u=new AppUser(); u.setName(name); u.setEmail(email.toLowerCase()); u.setPhone(phone); u.setPassword(enc.encode(password)); u.setRole(role);
    users.save(u);
    if(role.equals("RESTAURANT")){ var r=new Restaurant(); r.setName(restaurantName); r.setCuisine(cuisine); r.setLocation(location); r.setOwner(u); restaurants.save(r); }
    return "redirect:/login?registered";
  }
}
