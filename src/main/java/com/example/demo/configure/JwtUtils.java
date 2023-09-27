package com.example.demo.configure;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {
    
    private String SECRET_KEY = "secret";

    public String extractUsername(String token) {
        System.out.println("extractUsername");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
         System.out.println("extractExpiration");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        System.out.println("extractClaim");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
            System.out.println("extractAllClaims");
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
       
    }

    private Boolean isTokenExpired(String token) {
        System.out.println("isTokenExpired");
        return extractExpiration(token).before(new Date());
    }

    public  String generateToken(UserDetails userDetails) {
        System.out.println("generateToken");
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        System.out.println("createToken");
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        System.out.println("validateToken");
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}