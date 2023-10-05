package com.example.demo.configure;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.demo.dao.UserRepository;
import com.example.demo.exception.TokenException;
import com.example.demo.token.TokenRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class JwtAthFilter extends OncePerRequestFilter {

    private HandlerExceptionResolver exceptionResolver;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private TokenRepository tokenRepository;

    public JwtAthFilter(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)

            throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("doFilterInternal");
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String jwtToken;
      
            if (authHeader == null || !authHeader.startsWith("Bearer")) {
                System.out.println("authheader is null");
                filterChain.doFilter(request, response);
                return;
            }
            jwtToken = authHeader.substring(7);
            userEmail = jwtUtils.extractUsername(jwtToken);
            System.out.println(userEmail);
            try {
            if (userEmail != null) {
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userRepository.findByEmail(userEmail);
                    var userTokenFromDB = tokenRepository.findByToken(jwtToken);
                    System.out.println(userTokenFromDB);
                    if(userTokenFromDB.isEmpty()){
                        throw new TokenException(jwtToken, "token is not valid");
                    }
                    Boolean isTokenValid = userTokenFromDB
                            .map(t -> !t.getExpired() && !t.getRevoked()).orElse(false);
                    if (!isTokenValid) {
                        throw new ExpiredJwtException(null, null, "token is expired");
                    }
                    if (jwtUtils.validateToken(jwtToken, userDetails) && isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, userDetails.getAuthorities());
                        authToken.setDetails((new WebAuthenticationDetailsSource().buildDetails(request)));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            System.out.println("exception");
            System.out.println(ex);
            exceptionResolver.resolveException(request, response, null, ex);
        }
    }

}
