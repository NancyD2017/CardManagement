package com.example.TaskManagement.model.request;

import com.example.TaskManagement.validation.LoginRequestValid;
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
