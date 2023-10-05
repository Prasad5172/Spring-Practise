package com.example.demo.configure;


import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import com.example.demo.token.TokenRepository;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout (
        HttpServletRequest request,
         HttpServletResponse response, 
         Authentication authentication
         ){
       final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
       final String jwt ;
       if(authHeader == null || !authHeader.startsWith("Bearer")){
        return;
       }
       jwt = authHeader.substring(7);
       var storedToken = tokenRepository.findByToken(jwt).orElse(null);
       if(storedToken != null){
        storedToken.setExpired(true);
        storedToken.setRevoked(true);
        tokenRepository.save(storedToken);
       }
       response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK); // or another appropriate status code
    try (PrintWriter writer = response.getWriter()) {
        // Create a JSON response manually
        String jsonResponse = "{\"message\": \"Logout successful\"}";

        // Write the JSON response to the output
        writer.write(jsonResponse);
    } catch (IOException e) {
        e.printStackTrace(); // Handle the exception appropriately
    }
       
    } 
}
