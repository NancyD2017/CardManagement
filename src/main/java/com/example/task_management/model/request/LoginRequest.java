package com.example.task_management.model.request;

import com.example.task_management.validation.LoginRequestValid;
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
