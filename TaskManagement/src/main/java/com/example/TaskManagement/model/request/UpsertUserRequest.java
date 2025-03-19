package com.example.TaskManagement.model.request;

import com.example.TaskManagement.model.entity.Role;
import com.example.TaskManagement.validation.UserRequestValid;
import lombok.Data;

import java.util.Set;

@Data
@UserRequestValid
public class UpsertUserRequest {
    private String username;
    private String email;
    private Set<Role> roles;
    private String password;
}
