package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenException extends RuntimeException {
  // By Extending RuntimeException
  // RuntimeException is used for unchecked Exception which means exception at runtime 

  // By Extending Exception
  // Excetpion is used for checked exception which means exception occurs at compiletime
  private static final long serialVersionUID = 1L;

  public TokenException(String token, String message) {
    super(String.format("Failed for [%s]: %s", token, message));
  }
}
