package com.example.creditCardManagement.controller;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.model.entity.CardStatus;
import com.example.creditCardManagement.model.entity.CreditCard;
import com.example.creditCardManagement.model.entity.LimitDuration;
import com.example.creditCardManagement.model.request.CreditCardFilterRequest;
import com.example.creditCardManagement.model.request.UpsertCreditCardRequest;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.repository.CreditCardRepository;
import com.example.creditCardManagement.security.AppUserDetails;
import com.example.creditCardManagement.service.CreditCardService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardControllerTest {
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private CardHolderRepository cardHolderRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private AppUserDetails userDetails;
    @InjectMocks
    private CreditCardService creditCardService;

    private CardHolder cardHolder;
    private CreditCard creditCard;
    private UpsertCreditCardRequest request;

    @BeforeEach
    void setUp() {
        cardHolder = new CardHolder();
        cardHolder.setId(1L);
        creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setNumber(123456789L);
        creditCard.setCardHolder(cardHolder);
        creditCard.setBalance(1000.0);
        creditCard.setStatus(CardStatus.ACTIVE);
        creditCard.setDueToDate(LocalDate.now().plusYears(4).atStartOfDay());
        request = new UpsertCreditCardRequest();
        request.setCardHolderId(1L);
        request.setNumber(123456789L);
        request.setBalance(1000.0);
    }

    @Test
    void filterBy_success() {
        CreditCardFilterRequest filter = new CreditCardFilterRequest();
        filter.setCardHolderId(1L);
        filter.setPageNumber(0);
        filter.setPageSize(10);
        Page<CreditCard> page = new PageImpl<>(List.of(creditCard), PageRequest.of(0, 10), 1);
        when(creditCardRepository.findAllBySpecification(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<CreditCard> result = creditCardService.filterBy(filter);

        assertEquals(1, result.getContent().size());
        assertEquals(creditCard, result.getContent().get(0));
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        verify(creditCardRepository).findAllBySpecification(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void findAllByCardHolder_success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
        when(creditCardRepository.findByCardHolderId(1L)).thenReturn(List.of(creditCard));

        List<CreditCard> result = creditCardService.findAllByCardHolder();

        assertEquals(1, result.size());
        assertEquals(creditCard, result.get(0));
        verify(creditCardRepository).findByCardHolderId(1L);
    }

    @Test
    void findAllByCardHolder_unauthenticated_throwsIllegalStateException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        assertThrows(IllegalStateException.class, () -> creditCardService.findAllByCardHolder());
        verify(creditCardRepository, never()).findByCardHolderId(anyLong());
    }

    @Test
    void save_success() {
        when(creditCardRepository.findByNumber(123456789L)).thenReturn(Optional.empty());
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.of(cardHolder));
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        CreditCard result = creditCardService.save(creditCard, request);

        assertEquals(123456789L, result.getNumber());
        assertEquals(CardStatus.ACTIVE, result.getStatus());
        assertEquals(1000.0, result.getBalance());
        assertEquals(LocalDate.now().plusYears(4).atStartOfDay(), result.getDueToDate());
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    void save_nullNumber_throwsIllegalArgumentException() {
        creditCard.setNumber(null);

        assertThrows(IllegalArgumentException.class, () -> creditCardService.save(creditCard, request));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void save_existingNumber_throwsIllegalArgumentException() {
        when(creditCardRepository.findByNumber(123456789L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.save(creditCard, request));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void save_invalidCardHolderId_throwsIllegalArgumentException() {
        when(creditCardRepository.findByNumber(123456789L)).thenReturn(Optional.empty());
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.save(creditCard, request));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
        CreditCard toCard = new CreditCard();
        toCard.setId(2L);
        toCard.setCardHolder(cardHolder);
        toCard.setBalance(500.0);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setDueToDate(LocalDate.now().plusYears(4).atStartOfDay());

        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.findById(2L)).thenReturn(Optional.of(toCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);
        when(creditCardRepository.save(toCard)).thenReturn(toCard);

        List<CreditCard> result = creditCardService.commitTransaction(1L, 2L, 200.0);

        assertEquals(2, result.size());
        assertEquals(800.0, result.get(0).getBalance());
        assertEquals(700.0, result.get(1).getBalance());
        verify(creditCardRepository, times(2)).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_sameCard_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 1L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_nonPositiveAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 0.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_fromCardNotFound_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_toCardNotFound_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_inactiveCard_throwsIllegalArgumentException() {
        creditCard.setStatus(CardStatus.BLOCKED);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_exceedsLimit_throwsIllegalArgumentException() {
        creditCard.setLimit(100.0);
        creditCard.setLimitDate(LocalDateTime.now().plusDays(1));
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_insufficientFunds_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 2000.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void commitTransaction_expiredCard_throwsIllegalArgumentException() {
        creditCard.setDueToDate(LocalDate.now().minusDays(1).atStartOfDay());
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.findById(2L)).thenReturn(Optional.of(new CreditCard()));
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository).findById(1L);
        verify(creditCardRepository).findById(2L);
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void commitTransaction_differentOwners_throwsIllegalArgumentException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
        CreditCard toCard = new CreditCard();
        toCard.setId(2L);
        toCard.setCardHolder(new CardHolder());
        toCard.getCardHolder().setId(2L);
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setDueToDate(LocalDate.now().plusYears(4).atStartOfDay());

        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.commitTransaction(1L, 2L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        CreditCard result = creditCardService.withdrawMoney(1L, 200.0);

        assertEquals(800.0, result.getBalance());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void withdrawMoney_nonPositiveAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 0.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_cardNotFound_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_inactiveCard_throwsIllegalArgumentException() {
        creditCard.setStatus(CardStatus.BLOCKED);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_exceedsLimit_throwsIllegalArgumentException() {
        creditCard.setLimit(100.0);
        creditCard.setLimitDate(LocalDateTime.now().plusDays(1));
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_insufficientFunds_throwsIllegalArgumentException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 2000.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void withdrawMoney_expiredCard_throwsIllegalArgumentException() {
        creditCard.setDueToDate(LocalDate.now().minusDays(1).atStartOfDay());
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 200.0));
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void withdrawMoney_notOwned_throwsIllegalArgumentException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
        cardHolder.setId(2L);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.withdrawMoney(1L, 200.0));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void blockCreditCard_success() {
        creditCard.setStatus(CardStatus.ACTIVE);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        CreditCard result = creditCardService.blockCreditCard(1L);

        assertEquals(CardStatus.BLOCKED, result.getStatus());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void blockCreditCard_notFound_throwsEntityNotFoundException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> creditCardService.blockCreditCard(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void blockCreditCard_alreadyBlocked_throwsIllegalArgumentException() {
        creditCard.setStatus(CardStatus.BLOCKED);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.blockCreditCard(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void activateCreditCard_success() {
        creditCard.setStatus(CardStatus.BLOCKED);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        CreditCard result = creditCardService.activateCreditCard(1L);

        assertEquals(CardStatus.ACTIVE, result.getStatus());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void activateCreditCard_notFound_throwsEntityNotFoundException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> creditCardService.activateCreditCard(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void activateCreditCard_alreadyActive_throwsIllegalArgumentException() {
        creditCard.setStatus(CardStatus.ACTIVE);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.activateCreditCard(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void addLimit_success() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        CreditCard result = creditCardService.addLimit(1L, 500.0, LimitDuration.MONTH);

        assertEquals(500.0, result.getLimit());
        assertNotNull(result.getLimitDate());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void addLimit_nonPositiveLimit_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> creditCardService.addLimit(1L, 0.0, LimitDuration.MONTH));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void addLimit_notFound_throwsEntityNotFoundException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> creditCardService.addLimit(1L, 500.0, LimitDuration.MONTH));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void requestToBlock_success() {
        creditCard.setStatus(CardStatus.ACTIVE);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));
        when(creditCardRepository.save(creditCard)).thenReturn(creditCard);

        CreditCard result = creditCardService.requestToBlock(1L);

        assertEquals(CardStatus.REQUESTED_TO_BLOCK, result.getStatus());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    void requestToBlock_notFound_throwsEntityNotFoundException() {
        when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> creditCardService.requestToBlock(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void requestToBlock_alreadyRequestedOrBlocked_throwsIllegalArgumentException() {
        creditCard.setStatus(CardStatus.REQUESTED_TO_BLOCK);
        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(creditCard));

        assertThrows(IllegalArgumentException.class, () -> creditCardService.requestToBlock(1L));
        verify(creditCardRepository, never()).save(any(CreditCard.class));
    }

    @Test
    void deleteById_success() {
        when(creditCardRepository.existsById(1L)).thenReturn(true);

        creditCardService.deleteById(1L);

        verify(creditCardRepository).deleteById(1L);
    }

    @Test
    void deleteById_notFound_throwsEntityNotFoundException() {
        when(creditCardRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> creditCardService.deleteById(1L));
        verify(creditCardRepository, never()).deleteById(anyLong());
    }
}