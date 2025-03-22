package com.example.task_management.model.request;

import com.example.task_management.model.entity.Role;
import com.example.task_management.validation.UserRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@UserRequestValid
public class UpsertUserRequest {
    private String username;
    private String email;
    private Set<Role> roles;
    private String password;
}
