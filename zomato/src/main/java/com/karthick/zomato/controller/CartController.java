package com.karthick.zomato.controller;
import com.karthick.zomato.repository.FoodItemRepository;
import com.karthick.zomato.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequiredArgsConstructor
public class CartController {
  private final FoodItemRepository foods; private final CartService cs;
  @GetMapping("/cart")
  public String cart(HttpSession s,Model m){
    var lines=cs.lines(s); var t=cs.totals(lines,(String)s.getAttribute("coupon"));
    m.addAttribute("rows",lines); m.addAttribute("t",t); return "cart";
  }
  @PostMapping("/cart/add/{id}")
  public String add(@PathVariable Long id,HttpSession s,RedirectAttributes ra){
    var f=foods.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    var cart=cs.cart(s); String msg="Added "+f.getName()+" to cart";
    if(!cart.isEmpty()){
      var first=foods.findById(cart.keySet().iterator().next()).orElse(null);
      if(first!=null&&!first.getRestaurant().getId().equals(f.getRestaurant().getId())){
        cart.clear(); s.removeAttribute("coupon"); msg="Cart cleared - you can order from one restaurant at a time. Added "+f.getName(); }
    }
    cart.merge(id,1,Integer::sum); ra.addFlashAttribute("msg",msg);
    return "redirect:/restaurant/"+f.getRestaurant().getId();
  }
  @PostMapping("/cart/inc/{id}") public String inc(@PathVariable Long id,HttpSession s){ cs.cart(s).computeIfPresent(id,(k,v)->v+1); return "redirect:/cart"; }
  @PostMapping("/cart/dec/{id}") public String dec(@PathVariable Long id,HttpSession s){ var c=cs.cart(s); c.computeIfPresent(id,(k,v)->v<=1?null:v-1); return "redirect:/cart"; }
  @PostMapping("/cart/remove/{id}") public String remove(@PathVariable Long id,HttpSession s){ cs.cart(s).remove(id); return "redirect:/cart"; }
  @PostMapping("/cart/coupon")
  public String coupon(@RequestParam String code,HttpSession s){ s.setAttribute("coupon",code.trim()); return "redirect:/cart"; }
  @PostMapping("/cart/coupon/remove") public String couponRemove(HttpSession s){ s.removeAttribute("coupon"); return "redirect:/cart"; }
}
