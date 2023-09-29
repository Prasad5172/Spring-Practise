package com.example.demo.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    @Modifying
    int deleteByUser(Optional<User> optional);
   
    
    @Query("select t from Token t  where t.user.id = :userId and (t.revoked = false or t.expired=false)")
    List<Token> findAllValidTokensByUser(Integer userId);
}
