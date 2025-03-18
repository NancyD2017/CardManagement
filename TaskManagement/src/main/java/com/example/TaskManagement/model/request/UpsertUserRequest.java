package com.example.TaskManagement.model.request;

import lombok.Data;

import java.util.Set;

@Data
public class UpsertUserRequest {
    private String username;
    private String email;
    private Set<String> roles;
    private String password;
}
