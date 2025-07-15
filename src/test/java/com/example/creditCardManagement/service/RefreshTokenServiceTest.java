package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.RefreshToken;
import com.example.creditCardManagement.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", Duration.ofHours(1));
    }

    @Test
    void createRefreshToken_success() {
        RefreshToken token = new RefreshToken();
        token.setCardHolderId(1L);
        token.setToken("test-token");
        token.setExpiryDate(Instant.now().plusMillis(3600000));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(token);

        RefreshToken result = refreshTokenService.createRefreshToken(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCardHolderId());
        assertNotNull(result.getToken());
        assertNotNull(result.getExpiryDate());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}