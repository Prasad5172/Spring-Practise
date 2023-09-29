package com.example.demo.token;

import java.time.Instant;

import com.example.demo.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokentype;
    
    private Boolean expired;

    private Boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
}
