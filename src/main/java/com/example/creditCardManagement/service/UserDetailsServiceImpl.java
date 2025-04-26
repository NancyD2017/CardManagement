package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private final CardHolderRepository cardHolderRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws EntityNotFoundException {
        CardHolder cardHolder = cardHolderRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("CardHolder not found. Email is: " + email));
        return new AppUserDetails(cardHolder);
    }
}
