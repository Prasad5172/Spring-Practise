package com.example.demo.dao;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Token;
import java.util.Optional;


public interface TokenRepository  extends JpaRepository<Token,Integer>{
    Optional<Token> findByTokenId(String tokenId);
}
