package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.LoginRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@LoginRequestValid
public class LoginRequest {
    private String email;
    private String password;
}
