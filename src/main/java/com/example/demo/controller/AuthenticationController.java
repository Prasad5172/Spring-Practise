package com.example.demo.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.RequestsModels.AuthenticationRequest;
import com.example.demo.RequestsModels.RegisterRequest;
import com.example.demo.responceModels.JwtResponce;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("register")
    public ResponseEntity<JwtResponce> register(@RequestBody @Valid RegisterRequest request) {
       return authenticationService.register(request);
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponce> authenciate(@RequestBody @Valid AuthenticationRequest request) {
        return authenticationService.authenciate(request);
    }

    @PostMapping("refreshtoken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        authenticationService.refreshToken(request, response);
    }
    

    
}
