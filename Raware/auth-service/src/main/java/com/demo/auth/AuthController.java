
package com.demo.auth;
import io.jsonwebtoken.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/auth")
public class AuthController {
 @PostMapping("/login")
 public Map<String,String> login() {
  String token = Jwts.builder()
    .setSubject("user")
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis()+3600000))
    .signWith(SignatureAlgorithm.HS256,"secret")
    .compact();
  return Map.of("token", token);
 }
}
