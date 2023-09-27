package com.example.demo.RequestsModels;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "The firstname should not null")
    @NotEmpty(message = "The firstname should not empty")
    private String firstname;
    @NotNull(message = "The lastname should not null")
    @NotEmpty(message = "The lastname should not empty")
    private String lastname;
    @Email(message = "The email should valid")
    @NotNull(message = "The email should not null")
    @NotEmpty(message = "email should not be empty")
    private String email;
    @NotNull(message = "The password should not null")
    @NotEmpty(message = "The password should not empty")
    private String password;
}
