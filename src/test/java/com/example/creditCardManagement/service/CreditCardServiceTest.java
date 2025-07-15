package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.model.entity.CardStatus;
import com.example.creditCardManagement.model.entity.CreditCard;
import com.example.creditCardManagement.model.request.UpsertCreditCardRequest;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.repository.CreditCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private CardHolderRepository cardHolderRepository;
    @InjectMocks
    private CreditCardService creditCardService;

    private CardHolder cardHolder;
    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        cardHolder = new CardHolder();
        cardHolder.setId(1L);
        creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber(123456789L);
        creditCard.setBalance(1000.0);
        creditCard.setStatus(CardStatus.ACTIVE);
        creditCard.setDueToDate(LocalDateTime.now().plusYears(1));
        creditCard.setCardHolder(cardHolder);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void findAllByCardHolder_illegalStateException() {
        assertThrows(IllegalStateException.class, () -> creditCardService.findAllByCardHolder());
    }

    @Test
    void save_success() {
        UpsertCreditCardRequest request = new UpsertCreditCardRequest();
        request.setCardHolderId(1L);
        request.setBalance(1000.0);
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.of(cardHolder));
        when(creditCardRepository.findByNumber(anyLong())).thenReturn(Optional.empty());
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        CreditCard result = creditCardService.save(creditCard, request);

        assertEquals(creditCard, result);
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    void save_cardHolderNotFound_throwsIllegalArgumentException() {
        UpsertCreditCardRequest request = new UpsertCreditCardRequest();
        request.setCardHolderId(1L);
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.save(creditCard, request));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_success() {
        CreditCard toCard = new CreditCard();
        toCard.setId(2L);
        toCard.setBalance(500.0);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setDueToDate(LocalDateTime.now().plusYears(1));
        toCard.setCardHolder(cardHolder);

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
    }

    @Test
    void commitTransaction_cardNotFound_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }
}