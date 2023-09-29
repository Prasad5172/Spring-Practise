package com.example.demo.responceModels;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponce {
    private String accesstoken;
    private String refreshtoken;
}
