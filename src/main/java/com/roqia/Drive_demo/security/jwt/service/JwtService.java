package com.roqia.Drive_demo.security.jwt.service;
import com.roqia.Drive_demo.model.User;
import com.roqia.Drive_demo.repo.UserRepo;
import com.roqia.Drive_demo.security.jwt.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
  @Autowired
  private UserRepo userRepo;
    @Value("${jwt.secret}")
     private   String secret_key;

    private Key get_key(){
        byte[] keyBytes = Decoders.BASE64.decode(secret_key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generate_token(int userId) {
        User user = userRepo.findById(userId).get();
        String username = user.getName();
        String email = user.getEmail();
        String role = user.getRole();
        Map<String, Object> claims =new HashMap<>();
        claims.put("username",username);
        claims.put("email",email);
        claims.put("role",role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*5*60))
                .signWith(get_key(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generate_refresh_token(int userId) {
        User user = userRepo.findById(userId).get();
        String username = user.getName();
        String email = user.getEmail();
        String role = user.getRole();
        Map<String, Object> claims =new HashMap<>();
        claims.put("username",username);
        claims.put("email",email);
        claims.put("role",role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24*7))
                .signWith(get_key(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extract_All_claims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(get_key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public <T> T extract_claim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extract_All_claims(token);
        return claimsResolver.apply(claims);
    }
    public String extract_userId(String token){
        return extract_claim(token,Claims::getSubject);
    }
    public Date extract_expiration(String token){
        return extract_claim(token,Claims::getExpiration);
    }
    public Date extract_issuedDate(String token){
        return extract_claim(token,Claims::getIssuedAt);
    }
    public boolean token_expired(String token){
        return extract_expiration(token).before( new Date());
    }
    public String extract_userName(String token){
     return extract_claim(token, new Function<Claims, String>() {
        @Override
        public String apply(Claims claims) {
            return claims.get("username", String.class);
        }});}
    public String extract_userEmail(String token){
        return extract_claim(token, new Function<Claims, String>() {
            @Override
            public String apply(Claims claims) {
                return claims.get("email", String.class);
            }});}
    public boolean validate_token(String token, UserPrincipal userPrincipal){
        String userId = String.valueOf(userPrincipal.getUserId());
        return (extract_userId(token).equals(userId)&&!token_expired(token));
    }
}
