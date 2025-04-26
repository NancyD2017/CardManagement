package com.example.creditCardManagement.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_holder_id", nullable = false)
    private Long cardHolderId;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public RefreshToken(Long id, Long cardHolderId, String token, Instant expiryDate) {
        this.id = id;
        this.cardHolderId = cardHolderId;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}