package com.example.creditCardManagement.security;

import com.example.creditCardManagement.model.entity.CardHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class AppUserDetails implements UserDetails {

    private final CardHolder cardHolder;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return cardHolder.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .toList();
    }

    public Long getId() {
        return cardHolder.getId();
    }

    @Override
    public String getPassword() {
        return cardHolder.getPassword();
    }

    @Override
    public String getUsername() {
        return cardHolder.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
