package com.example.demo.auth;

import java.util.Optional;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.RequestsModels.AuthenticationRequest;
import com.example.demo.RequestsModels.RefreshTokenRequest;
import com.example.demo.RequestsModels.RegisterRequest;
import com.example.demo.configure.JwtUtils;
import com.example.demo.dao.UserRepository;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.responceModels.JwtResponce;
import com.example.demo.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        // System.out.println(request);
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok("User added to the DB");
    }

    @PostMapping("authenticate")
    public ResponseEntity<JwtResponce> authenciate(@RequestBody @Valid AuthenticationRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (authentication.isAuthenticated()) {
            var user = userRepository.findByEmail(request.getEmail());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());
            var jwt = jwtUtils.generateToken(user);
            return ResponseEntity.ok(JwtResponce
                    .builder()
                    .accesstoken(jwt)
                    .token(refreshToken.getToken())
                    .build());
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("refreshtoken")
    public ResponseEntity<JwtResponce> refreshToken(@Valid @RequestBody RefreshTokenRequest token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(token.getToken());

        if (refreshTokenOpt.isPresent()) {
            RefreshToken refreshToken = refreshTokenOpt.get();
            refreshToken = refreshTokenService.verifytoken(refreshToken);

            String accessToken = jwtUtils.generateToken(refreshToken.getUser());

            JwtResponce response = JwtResponce.builder()
                    .accesstoken(accessToken)
                    .token(token.getToken())
                    .build();

            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("User not found in db");
        }
    }

}
