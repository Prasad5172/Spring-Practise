package com.example.demo.token;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TokenService {
    
    @Autowired
    private TokenRepository refreshTokenRepository;

   
    
    public Optional<Token> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    
    
    
}
