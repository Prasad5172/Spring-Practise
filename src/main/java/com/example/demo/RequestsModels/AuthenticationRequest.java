package com.example.demo.RequestsModels;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
    @Email(message = "The email should valid")
    @NotNull(message = "The email should not null")
    @NotEmpty(message = "email should not be empty")
    private String email;
    @NotNull(message = "The password should not null")
    @NotEmpty(message = "password should not be empty")
    private String password;
}
