package com.example.creditCardManagement.service;

import com.example.creditCardManagement.filter.CreditCardFilter;
import com.example.creditCardManagement.filter.CreditCardSpecification;
import com.example.creditCardManagement.model.entity.CardStatus;
import com.example.creditCardManagement.model.entity.CreditCard;
import com.example.creditCardManagement.model.entity.LimitDuration;
import com.example.creditCardManagement.model.request.CreditCardFilterRequest;
import com.example.creditCardManagement.model.request.UpsertCreditCardRequest;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.repository.CreditCardRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final CardHolderRepository cardHolderRepository;

    public List<CreditCard> filterBy(CreditCardFilterRequest filter) {
        CreditCardFilter creditCardFilter = new CreditCardFilter();
        creditCardFilter.setCardHolderId(filter.getCardHolderId());
        creditCardFilter.setTransactionHistory(filter.getTransactionHistory());
        creditCardFilter.setPageNumber(Objects.requireNonNullElse(filter.getPageNumber(), 0));
        creditCardFilter.setPageSize(Objects.requireNonNullElse(filter.getPageSize(), 10));
        return creditCardRepository.findAll(CreditCardSpecification.withFilter(creditCardFilter), creditCardFilter.toPageable());
    }

    public List<CreditCard> findAllByCardHolder() {
        Long cardHolderId = findCardHolderId();
        if (cardHolderId == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return creditCardRepository.findByCardHolderId(cardHolderId);
    }

    @Transactional
    public CreditCard save(CreditCard creditCard, UpsertCreditCardRequest request) {
        if (creditCardRepository.findByNumber(creditCard.getNumber()).isPresent()) {
            throw new IllegalArgumentException("CreditCard with number " + creditCard.getNumber() + " already exists");
        }
        setCardHolder(creditCard, request);
        if (creditCard.getCardHolder() == null) {
            throw new IllegalArgumentException("Invalid cardHolderId");
        }
        creditCard.setDueToDate(LocalDate.now().plusYears(4).atStartOfDay());
        creditCard.setBalance(0.0);
        creditCard.setStatus(CardStatus.ACTIVE);
        CreditCard savedCard = creditCardRepository.save(creditCard);
        updateCardHolder(savedCard);
        return savedCard;
    }

    @Transactional
    public List<CreditCard> commitTransaction(Long fromId, Long toId, Double amount) {
        if (Objects.equals(fromId, toId)) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }
        CreditCard fromCard = creditCardRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard with id " + fromId + " not found"));
        CreditCard toCard = creditCardRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard with id " + toId + " not found"));

        validateTransaction(fromCard, amount);
        validateCardOwnership(fromCard, toCard);

        fromCard.setBalance(fromCard.getBalance() - amount);
        fromCard.addTransaction(LocalDateTime.now() + ": Transfer to card " + toId + " " + amount + " roubles");
        toCard.setBalance(toCard.getBalance() + amount);
        toCard.addTransaction(LocalDateTime.now() + ": Transfer from card " + fromId + " " + amount + " roubles");

        CreditCard savedFrom = creditCardRepository.save(fromCard);
        CreditCard savedTo = creditCardRepository.save(toCard);
        updateCardHolder(savedFrom);
        updateCardHolder(savedTo);
        return List.of(savedFrom, savedTo);
    }

    @Transactional
    public CreditCard withdrawMoney(Long fromId, Double amount) {
        CreditCard fromCard = creditCardRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard with id " + fromId + " not found"));

        validateTransaction(fromCard, amount);
        if (!fromCard.getCardHolder().getId().equals(findCardHolderId())) {
            throw new IllegalArgumentException("Card with id " + fromId + " does not belong to user");
        }

        fromCard.setBalance(fromCard.getBalance() - amount);
        fromCard.addTransaction(LocalDateTime.now() + ": Withdrawal " + amount + " roubles");

        CreditCard savedCard = creditCardRepository.save(fromCard);
        updateCardHolder(savedCard);
        return savedCard;
    }

    @Transactional
    public CreditCard blockCreditCard(Long id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditCard with id " + id + " not found"));
        card.setStatus(CardStatus.BLOCKED);
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard activateCreditCard(Long id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditCard with id " + id + " not found"));
        card.setStatus(CardStatus.ACTIVE);
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard addLimit(Long id, Double limit, LimitDuration limitDuration) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditCard with id " + id + " not found"));
        card.setLimit(limit);
        card.setLimitDate(switch (limitDuration) {
            case YEAR -> LocalDate.now().plusYears(1).atStartOfDay();
            case MONTH -> LocalDate.now().plusMonths(1).atStartOfDay();
            case SEASON -> LocalDate.now().plusMonths(3).atStartOfDay();
            case DAY -> LocalDate.now().plusDays(1).atStartOfDay();
        });
        card.addTransaction(LocalDateTime.now() + ": Added limit " + limit + " per " + limitDuration.name().toLowerCase());
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard requestToBlock(Long id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CreditCard with id " + id + " not found"));
        card.setStatus(CardStatus.REQUESTED_TO_BLOCK);
        card.addTransaction(LocalDateTime.now() + ": Request to block the credit card");
        return creditCardRepository.save(card);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!creditCardRepository.existsById(id)) {
            throw new EntityNotFoundException("CreditCard with id " + id + " not found");
        }
        creditCardRepository.deleteById(id);
    }

    private void setCardHolder(CreditCard creditCard, UpsertCreditCardRequest request) {
        if (request.getCardHolderId() != null) {
            creditCard.setCardHolder(cardHolderRepository.findById(request.getCardHolderId())
                    .orElseThrow(() -> new IllegalArgumentException("CardHolder with id " + request.getCardHolderId() + " not found")));
        }
    }

    private void updateCardHolder(CreditCard creditCard) {
        if (creditCard.getCardHolder() != null) {
            creditCard.getCardHolder().addCard(creditCard);
        }
    }

    private Long findCardHolderId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails &&
                userDetails instanceof com.example.creditCardManagement.security.AppUserDetails appUserDetails) {
            return appUserDetails.getId();
        }
        return null;
    }

    private void validateTransaction(CreditCard card, Double amount) {
        if (LocalDateTime.now().isBefore(card.getLimitDate()) && card.getLimit() < amount) {
            throw new IllegalArgumentException("Amount exceeds limit for card " + card.getId());
        }
        if (card.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds for card " + card.getId());
        }
        if (LocalDateTime.now().isAfter(card.getDueToDate())) {
            card.setStatus(CardStatus.EXPIRED);
            creditCardRepository.save(card);
            throw new IllegalArgumentException("Card with id " + card.getId() + " is expired");
        }
    }

    private void validateCardOwnership(CreditCard fromCard, CreditCard toCard) {
        Long userId = findCardHolderId();
        if (!fromCard.getCardHolder().getId().equals(toCard.getCardHolder().getId()) ||
                !fromCard.getCardHolder().getId().equals(userId)) {
            throw new IllegalArgumentException("One or both cards do not belong to the user");
        }
    }
}