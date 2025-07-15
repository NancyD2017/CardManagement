package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    private CardHolderRepository cardHolderRepository;
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private CardHolder cardHolder;

    @BeforeEach
    void setUp() {
        cardHolder = new CardHolder();
        cardHolder.setEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_success() {
        when(cardHolderRepository.findByEmail("test@example.com")).thenReturn(Optional.of(cardHolder));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertTrue(result instanceof AppUserDetails);
        verify(cardHolderRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_notFound_throwsEntityNotFoundException() {
        when(cardHolderRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userDetailsService.loadUserByUsername("test@example.com"));
        verify(cardHolderRepository).findByEmail("test@example.com");
    }
}