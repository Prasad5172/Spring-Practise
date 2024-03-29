package com.example.demo.exception;



import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;


@RestControllerAdvice
public class ApiExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception ex){
        System.out.println("ExceptionHandler");
        System.out.println(ex);
        ProblemDetail errorDetail = null;
        if(ex instanceof BadCredentialsException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("Reason", "Authentication failure ");
        }
      
        if(ex instanceof AccessDeniedException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Not Authorized ");
        }

        if(ex instanceof MethodArgumentNotValidException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
            errorDetail.setProperty("Reason", "Enter Valid Details ");
        }
        if(ex instanceof SignatureException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
            errorDetail.setProperty("Reason", "Enter Valid Details ");
        }
        if(ex instanceof ExpiredJwtException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Jwt token is  expired");
        }
        if(ex instanceof SignatureException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Not a valid signature in jwt");
        }
        if(ex instanceof MalformedJwtException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Not a valid jwt");
        }
        if(ex instanceof IllegalArgumentException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Not a valid jwt");
        }
        if(ex instanceof UsernameNotFoundException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), ex.getMessage());
            errorDetail.setProperty("Reason", "Email not found");
        }
        if(ex instanceof CustomException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
            errorDetail.setProperty("Reason", "Email is already registred");
        }
        if(ex instanceof TokenException){
            errorDetail= ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), ex.getMessage());
            errorDetail.setProperty("Reason", "Token Exception");
        }
        return errorDetail;
    }

   




}
