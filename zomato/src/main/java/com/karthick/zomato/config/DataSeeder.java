package com.karthick.zomato.config;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
  private final UserRepository users; private final RestaurantRepository restaurants;
  private final FoodItemRepository foods; private final CouponRepository coupons; private final PasswordEncoder enc;
  static String u(String id){ return "https://images.unsplash.com/photo-"+id+"?auto=format&fit=crop&w=600&q=80"; }
  AppUser user(String n,String e,String role,String pw){ var x=new AppUser(); x.setName(n); x.setEmail(e); x.setRole(role); x.setPassword(enc.encode(pw)); x.setPhone("9876543210"); return users.save(x); }
  Restaurant rest(String n,String c,String l,double r,String img,AppUser owner){ var x=new Restaurant(); x.setName(n); x.setCuisine(c); x.setLocation(l); x.setRating(r); x.setImageUrl(u(img)); x.setOwner(owner); return restaurants.save(x); }
  void food(Restaurant r,String n,String d,String p,String cat,String img,boolean veg){ var f=new FoodItem(); f.setRestaurant(r); f.setName(n); f.setDescription(d); f.setPrice(new BigDecimal(p)); f.setCategory(cat); f.setImageUrl(u(img)); f.setVeg(veg); foods.save(f); }
  void coupon(String code,int pct,String max,String min){ var c=new Coupon(); c.setCode(code); c.setPercent(pct); c.setMaxDiscount(new BigDecimal(max)); c.setMinOrder(new BigDecimal(min)); coupons.save(c); }
  @Override public void run(String... args){
    if(users.count()>0) return;
    user("Admin","admin@zomato.com","ADMIN","Admin@123");
    var owner=user("Biryani House Owner","owner@zomato.com","RESTAURANT","Owner@123");
    user("Demo Customer","demo@zomato.com","CUSTOMER","Demo@123");
    var r1=rest("Biryani House","Biryani · Mughlai","Tiruppur",4.5,"1563379091339-03b21ab4a4f8",owner);
    var r2=rest("Pizza Corner","Pizza · Italian","Coimbatore",4.2,"1513104890138-7c749659a591",null);
    var r3=rest("Dosa Junction","South Indian · Breakfast","Tiruppur",4.7,"1668236543090-82eba5ee5976",null);
    var r4=rest("Burger Barn","Burgers · Fast Food","Erode",4.0,"1568901346375-23c9450c58cd",null);
    var r5=rest("Dragon Wok","Chinese · Noodles","Salem",4.1,"1555126634-323283e090fa",null);
    var r6=rest("Sweet Tooth","Desserts · Bakery","Tiruppur",4.6,"1578985545062-69928b1d9587",null);
    food(r1,"Chicken Dum Biryani","Basmati rice slow-cooked with tender chicken","249","Biryani","1563379091339-03b21ab4a4f8",false);
    food(r1,"Mutton Biryani","Rich mutton pieces, saffron rice","329","Biryani","1585937421612-70a008356fbe",false);
    food(r1,"Veg Biryani","Seasonal vegetables and fragrant rice","179","Biryani","1589302168068-964664d93dc0",true);
    food(r1,"Paneer Butter Masala","Creamy tomato gravy with paneer","219","Curries","1565557623262-b51c2513a641",true);
    food(r2,"Margherita Pizza","Mozzarella and fresh basil","229","Pizza","1574071318508-1cdbab80d002",true);
    food(r2,"Pepperoni Pizza","Loaded with spicy pepperoni","299","Pizza","1565299624946-b28f40a0ae38",false);
    food(r2,"Veggie Supreme","Capsicum, olives, onion, corn","279","Pizza","1513104890138-7c749659a591",true);
    food(r2,"Garlic Bread","Toasted with herbed butter","119","Sides","1573140247632-f8fd74997d5c",true);
    food(r3,"Masala Dosa","Potato masala, chutney, sambar","90","South Indian","1668236543090-82eba5ee5976",true);
    food(r3,"Idli Sambar","Soft idlis with hot sambar","60","South Indian","1589301760014-d929f3979dbc",true);
    food(r3,"Medu Vada","Crispy lentil fritters","70","South Indian","1630383249896-424e482df921",true);
    food(r3,"Filter Coffee","Traditional South Indian coffee","40","Beverages","1509042239860-f550ce710b93",true);
    food(r4,"Chicken Burger","Crispy chicken, lettuce, mayo","149","Burgers","1568901346375-23c9450c58cd",false);
    food(r4,"Cheese Burger","Double cheese, grilled patty","169","Burgers","1550547660-d9450f859349",false);
    food(r4,"Veggie Burger","Spiced veg patty","129","Burgers","1520072959219-c595dc870360",true);
    food(r4,"French Fries","Salted, crispy","99","Sides","1573080496219-bb080dd4f877",true);
    food(r5,"Veg Hakka Noodles","Wok-tossed noodles","149","Chinese","1555126634-323283e090fa",true);
    food(r5,"Chicken Fried Rice","Egg and chicken fried rice","179","Chinese","1603133872878-684f208fb84b",false);
    food(r5,"Gobi Manchurian","Crispy cauliflower in sauce","159","Chinese","1512058564366-18510be2db19",true);
    food(r6,"Chocolate Cake","Rich dark chocolate slice","199","Desserts","1578985545062-69928b1d9587",true);
    food(r6,"Glazed Donuts","Pack of 4","129","Desserts","1551024601-bec78aea704b",true);
    food(r6,"Pancakes","Maple syrup and butter","169","Desserts","1567620905732-2d1ec7ab7445",true);
    food(r6,"Ice Cream Sundae","Three scoops with toppings","149","Desserts","1563805042-7684c019e1cb",true);
    coupon("WELCOME20",20,"100","199"); coupon("FEAST10",10,"50","0");
  }
}
