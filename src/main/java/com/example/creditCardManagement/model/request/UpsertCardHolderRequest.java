package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.Role;
import com.example.creditCardManagement.validation.CardHolderRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CardHolderRequestValid
public class UpsertCardHolderRequest {
    private String username;
    private String email;
    private Set<Role> roles;
    private String password;
}
