package com.karthick.zomato.service;
import com.karthick.zomato.model.*;
import com.karthick.zomato.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.*;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
@Service @RequiredArgsConstructor
public class CartService {
  private final FoodItemRepository foods; private final CouponRepository coupons;
  @Getter @AllArgsConstructor public static class Line { private FoodItem food; private int quantity; private BigDecimal subtotal; }
  @Getter @AllArgsConstructor public static class Totals { private BigDecimal subtotal, tax, deliveryFee, discount, total; private String couponCode, couponMessage; }
  @SuppressWarnings("unchecked")
  public Map<Long,Integer> cart(HttpSession s){
    Object o=s.getAttribute("cart");
    if(o==null){ var m=new LinkedHashMap<Long,Integer>(); s.setAttribute("cart",m); return m; }
    return (Map<Long,Integer>)o;
  }
  public List<Line> lines(HttpSession s){
    var out=new ArrayList<Line>();
    for(var e: cart(s).entrySet()) foods.findById(e.getKey()).filter(FoodItem::isAvailable)
      .ifPresent(f -> out.add(new Line(f,e.getValue(),f.getPrice().multiply(BigDecimal.valueOf(e.getValue())))));
    return out;
  }
  public Totals totals(List<Line> lines, String code){
    BigDecimal sub=lines.stream().map(Line::getSubtotal).reduce(BigDecimal.ZERO,BigDecimal::add);
    BigDecimal tax=sub.multiply(new BigDecimal("0.05")).setScale(2,RoundingMode.HALF_UP);
    BigDecimal del=lines.isEmpty()||sub.compareTo(new BigDecimal("500"))>=0?BigDecimal.ZERO:new BigDecimal("40");
    BigDecimal disc=BigDecimal.ZERO; String applied=null,msg=null;
    if(code!=null && !lines.isEmpty()){
      var c=coupons.findByCodeIgnoreCaseAndActiveTrue(code);
      if(c.isEmpty()) msg="Invalid or expired coupon";
      else {
        BigDecimal min=c.get().getMinOrder()==null?BigDecimal.ZERO:c.get().getMinOrder();
        BigDecimal max=c.get().getMaxDiscount()==null?sub:c.get().getMaxDiscount();
        if(sub.compareTo(min)<0) msg="Add items worth ₹"+min+" to use this coupon";
        else { disc=sub.multiply(BigDecimal.valueOf(c.get().getPercent())).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP).min(max);
               applied=c.get().getCode(); msg=applied+" applied"; }
      }
    }
    return new Totals(sub,tax,del,disc,sub.add(tax).add(del).subtract(disc),applied,msg);
  }
}
