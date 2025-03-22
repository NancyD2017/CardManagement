package com.example.task_management.model.response;

import com.example.task_management.model.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}
