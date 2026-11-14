package com.karthick.zomato.controller;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import com.karthick.zomato.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;
@Controller @RequiredArgsConstructor
public class OrderController {
  private final CartService cs; private final FoodOrderRepository orders; private final UserRepository users;
  static final List<String> STEPS=List.of("PLACED","ACCEPTED","PREPARING","OUT_FOR_DELIVERY","DELIVERED");
  @GetMapping("/checkout")
  public String checkout(HttpSession s,Model m,Principal p){
    var lines=cs.lines(s); if(lines.isEmpty()) return "redirect:/cart";
    m.addAttribute("rows",lines); m.addAttribute("t",cs.totals(lines,(String)s.getAttribute("coupon")));
    m.addAttribute("me",users.findByEmail(p.getName()).orElseThrow()); return "checkout";
  }
  @PostMapping("/checkout") @Transactional
  public String place(@RequestParam String customerName,@RequestParam String phone,@RequestParam String deliveryAddress,
      @RequestParam String paymentMethod,HttpSession s,Principal p,RedirectAttributes ra){
    var lines=cs.lines(s); if(lines.isEmpty()) return "redirect:/cart";
    if(!phone.matches("[0-9]{10}")||deliveryAddress.isBlank()||customerName.isBlank()){ ra.addFlashAttribute("msg","Enter a valid name, 10-digit phone and address"); return "redirect:/checkout"; }
    var t=cs.totals(lines,(String)s.getAttribute("coupon"));
    var o=new FoodOrder(); o.setUser(users.findByEmail(p.getName()).orElseThrow()); o.setRestaurant(lines.get(0).getFood().getRestaurant());
    o.setCustomerName(customerName); o.setPhone(phone); o.setDeliveryAddress(deliveryAddress);
    o.setSubtotal(t.getSubtotal()); o.setTax(t.getTax()); o.setDeliveryFee(t.getDeliveryFee()); o.setDiscount(t.getDiscount()); o.setTotalAmount(t.getTotal()); o.setCouponCode(t.getCouponCode());
    boolean online="ONLINE".equals(paymentMethod);
    o.setPaymentMethod(online?"ONLINE (demo)":"COD"); o.setPaymentStatus(online?"PAID":"PENDING");
    for(var l: lines){ var it=new OrderItem(); it.setOrder(o); it.setFoodName(l.getFood().getName()); it.setPrice(l.getFood().getPrice()); it.setQuantity(l.getQuantity()); o.getItems().add(it); }
    orders.save(o); s.removeAttribute("cart"); s.removeAttribute("coupon");
    return "redirect:/orders/"+o.getId()+"/success";
  }
  private FoodOrder mine(Long id,Principal p){
    var o=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    if(!o.getUser().getEmail().equals(p.getName())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    return o;
  }
  @GetMapping("/orders") public String history(Principal p,Model m){ m.addAttribute("orders",orders.findByUserEmailOrderByCreatedAtDesc(p.getName())); return "orders"; }
  @GetMapping("/orders/{id}/success") public String success(@PathVariable Long id,Principal p,Model m){ m.addAttribute("order",mine(id,p)); return "success"; }
  @GetMapping("/orders/{id}")
  public String detail(@PathVariable Long id,Principal p,Model m){
    var o=mine(id,p); m.addAttribute("order",o); m.addAttribute("steps",STEPS); m.addAttribute("idx",STEPS.indexOf(o.getStatus())); return "order";
  }
  @GetMapping("/orders/{id}/status") @ResponseBody public String status(@PathVariable Long id,Principal p){ return mine(id,p).getStatus(); }
  @PostMapping("/orders/{id}/cancel")
  public String cancel(@PathVariable Long id,Principal p,RedirectAttributes ra){
    var o=mine(id,p);
    if("PLACED".equals(o.getStatus())){ o.setStatus("CANCELLED"); if("PAID".equals(o.getPaymentStatus())) o.setPaymentStatus("REFUND_PENDING"); orders.save(o); ra.addFlashAttribute("msg","Order cancelled"); }
    else ra.addFlashAttribute("msg","Order can no longer be cancelled");
    return "redirect:/orders/"+id;
  }
}
