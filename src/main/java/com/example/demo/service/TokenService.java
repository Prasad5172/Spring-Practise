package com.example.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.TokenRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.model.Token;

@Service
public class TokenService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public Token createRefreshToken(String name ){
        Token refreshToken = Token.builder()
                    .user(userRepository.findByEmail(name))
                    .tokenId(UUID.randomUUID().toString())   //unique id not the jwt token storing the jwt token id not the jwt 
                    .expiredate(Instant.now().plusMillis(1000*60*2))
                    .build();
        return tokenRepository.save(refreshToken);
    }

    public Optional<Token> findByToken(String token){
        return tokenRepository.findByTokenId(token);
    }

    public Token verifytoken(Token token ){
        if(token.getExpiredate().compareTo(Instant.now())<0){
            tokenRepository.delete(token);
            throw new RuntimeException(token.getTokenId() + "token is expired");
        }
        return token;
    }
}
