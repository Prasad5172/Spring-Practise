package com.example.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.RefreshTokenRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.model.RefreshToken;

@Service
public class RefreshTokenService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    public RefreshToken createRefreshToken(String name ){
        RefreshToken refreshToken = RefreshToken.builder()
                    .user(userRepository.findByEmail(name))
                    .token(UUID.randomUUID().toString())   //unique id not the jwt token storing the jwt token id not the jwt 
                    .expiredate(Instant.now().plusMillis(1000*60*2))
                    .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifytoken(RefreshToken token ){
        if(token.getExpiredate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + "token is expired");
        }
        return token;
    }
}
