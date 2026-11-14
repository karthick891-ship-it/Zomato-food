package com.karthick.zomato.config;
import com.karthick.zomato.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {
  private final UserRepository users;
  @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    var u = users.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user"));
    return User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRole()).build();
  }
}
