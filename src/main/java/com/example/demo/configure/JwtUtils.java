package com.example.demo.configure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${user.app.jwtSecret}")
    private String SECRET_KEY;

    @Value("${user.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${user.app.jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    public String extractUsername(String token) {
        // System.out.println("extractUsername");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        // System.out.println("extractExpiration");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // System.out.println("extractClaim");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        // System.out.println("extractAllClaims");
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

    }

    public Boolean isTokenExpired(String token) {
        // System.out.println("isTokenExpired");
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        // System.out.println("generateToken");
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        // System.out.println("createToken"+jwtExpirationMs);
        return buildJwt(claims, userDetails, jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        // System.out.println("createRefreshToken "+ jwtRefreshExpirationMs);
        return buildJwt(new HashMap<>(), userDetails, jwtRefreshExpirationMs);
    }

    public String buildJwt(
            Map<String, Object> claims,
            UserDetails userDetails,
            long expiration) {
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        // System.out.println("validateToken");
        final String username = extractUsername(token);
        try {
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (SignatureException e) {
            throw new SignatureException("Invalid JWT signature:");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid JWT token ");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "JWT token is expired");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JWT claims string is empty");
        }
    }

}