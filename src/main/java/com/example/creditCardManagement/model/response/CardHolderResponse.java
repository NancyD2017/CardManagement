package com.example.creditCardManagement.model.response;

import com.example.creditCardManagement.model.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class CardHolderResponse {
    private Long id;
    private String username;
    private String email;
    private Set<Role> roles;
}
