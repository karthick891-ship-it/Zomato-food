package com.karthick.zomato.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableWebSecurity
public class SecurityConfig {
  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
  @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(a -> a
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .requestMatchers("/owner/**").hasRole("RESTAURANT")
        .requestMatchers("/checkout","/orders/**","/favorite/**","/favorites","/restaurant/*/review").authenticated()
        .anyRequest().permitAll())
      .formLogin(f -> f.loginPage("/login").successHandler((req,res,auth) -> {
          String t="/"; String r=auth.getAuthorities().toString();
          if(r.contains("ROLE_ADMIN")) t="/admin"; else if(r.contains("ROLE_RESTAURANT")) t="/owner";
          res.sendRedirect(t); }).permitAll())
      .logout(l -> l.logoutSuccessUrl("/"));
    return http.build();
  }
}
