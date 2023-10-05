package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/test")
public class TestController {

  @GetMapping("all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("user")
  @PreAuthorize("hasAuthority('USER') ")
  public String userAccess() {
    return "User Content.";
  }

  @GetMapping("admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public String adminAccess() {
    return "Admin Board.";
  }

 
}
