package com.example.demo.controller;

import java.io.IOException;
import java.lang.*;
import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.RequestsModels.AuthenticationRequest;
import com.example.demo.RequestsModels.RegisterRequest;
import com.example.demo.configure.JwtUtils;
import com.example.demo.dao.UserRepository;
import com.example.demo.email.EmailService;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.responceModels.JwtResponce;
import com.example.demo.token.Token;
import com.example.demo.token.TokenRepository;
import com.example.demo.token.TokenType;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    public ResponseEntity<JwtResponce> register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var saveUser = userRepository.save(user);
        var jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(saveUser, jwtToken);
        String link = "http://localhost:8080/api/v1//auth/register/confirm?token=" + jwtToken;
        emailService.send(
                user.getEmail(),
                buildEmail(request.getFirstname(), link));

        return ResponseEntity.ok(generateJwtResponce(jwtToken, refreshToken));
    }

    public ResponseEntity<JwtResponce> authenciate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        if (authentication.isAuthenticated()) {
            var user = userRepository.findByEmail(request.getEmail());
            var jwtToken = jwtUtils.generateToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            String refreshToken = jwtUtils.generateRefreshToken(user);
            return ResponseEntity.ok(generateJwtResponce(jwtToken, refreshToken));
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }
    
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String userEmail;
        final String refreshToken;

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new NullPointerException("Tokens is null or must start with Bearer");
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtUtils.extractUsername(refreshToken);
        System.out.println(userEmail);
        if (userEmail != null) {
            var user = userRepository.findByEmail(userEmail);
            if (jwtUtils.validateToken(refreshToken, user)) {
                var accessToken = jwtUtils.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                JwtResponce authResponce = JwtResponce.builder()
                        .accesstoken(accessToken)
                        .refreshtoken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponce);
            }
        }
    }


    public void revokeAllUserTokens(User user) {
        var validtoken = tokenRepository.findAllValidTokensByUser(user.getId());
        System.out.println(validtoken);
        if (validtoken.isEmpty()) {
            return;
        }
        validtoken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validtoken);
    }

    public Token saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokentype(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
        return token;
    }

    public JwtResponce generateJwtResponce(String accessToken, String refreshToken) {
        return JwtResponce.builder()
                .accesstoken(accessToken)
                .refreshtoken(refreshToken)
                .build();
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
                +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
                +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
                +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n"
                +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
                +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
                +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
                +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name
                + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\""
                + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    
}
