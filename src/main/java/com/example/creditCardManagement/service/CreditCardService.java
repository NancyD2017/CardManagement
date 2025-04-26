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
import org.springframework.data.domain.Page;
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

    public Page<CreditCard> filterBy(CreditCardFilterRequest filter) {
        CreditCardFilter creditCardFilter = new CreditCardFilter();
        creditCardFilter.setCardHolderId(filter.getCardHolderId());
        creditCardFilter.setTransactionHistory(filter.getTransactionHistory());
        creditCardFilter.setPageNumber(Objects.requireNonNullElse(filter.getPageNumber(), 0));
        creditCardFilter.setPageSize(Objects.requireNonNullElse(filter.getPageSize(), 10));
        return creditCardRepository.findAllBySpecification(CreditCardSpecification.withFilter(creditCardFilter), creditCardFilter.toPageable());
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
        if (creditCard.getNumber() == null) {
            throw new IllegalArgumentException("Card number is required");
        }
        if (creditCardRepository.findByNumber(creditCard.getNumber()).isPresent()) {
            throw new IllegalArgumentException("Credit card with number " + creditCard.getNumber() + " already exists");
        }
        setCardHolder(creditCard, request);
        if (creditCard.getCardHolder() == null) {
            throw new IllegalArgumentException("Invalid cardHolderId");
        }
        creditCard.setDueToDate(LocalDate.now().plusYears(4).atStartOfDay());
        creditCard.setBalance(request.getBalance() == null ? 0.0D : request.getBalance());
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
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        CreditCard fromCard = creditCardRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Credit card with id " + fromId + " not found"));
        CreditCard toCard = creditCardRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Credit card with id " + toId + " not found"));

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
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        CreditCard fromCard = creditCardRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Credit card with id " + fromId + " not found"));

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
                .orElseThrow(() -> new EntityNotFoundException("Credit card with id " + id + " not found"));
        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalArgumentException("Card with id " + id + " is already blocked");
        }
        card.setStatus(CardStatus.BLOCKED);
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard activateCreditCard(Long id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Credit card with id " + id + " not found"));
        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Card with id " + id + " is already active");
        }
        card.setStatus(CardStatus.ACTIVE);
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard addLimit(Long id, Double limit, LimitDuration limitDuration) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Credit card with id " + id + " not found"));
        card.setLimit(limit);
        card.setLimitDate(switch (limitDuration) {
            case YEAR -> LocalDateTime.now().plusYears(1);
            case MONTH -> LocalDateTime.now().plusMonths(1);
            case SEASON -> LocalDateTime.now().plusMonths(3);
            case DAY -> LocalDateTime.now().plusDays(1);
        });
        card.addTransaction(LocalDateTime.now() + ": Added limit " + limit + " per " + limitDuration.name().toLowerCase());
        return creditCardRepository.save(card);
    }

    @Transactional
    public CreditCard requestToBlock(Long id) {
        CreditCard card = creditCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Credit card with id " + id + " not found"));
        if (card.getStatus() == CardStatus.REQUESTED_TO_BLOCK || card.getStatus() == CardStatus.BLOCKED) {
            throw new IllegalArgumentException("Card with id " + id + " is already requested to block or blocked");
        }
        card.setStatus(CardStatus.REQUESTED_TO_BLOCK);
        card.addTransaction(LocalDateTime.now() + ": Request to block the credit card");
        return creditCardRepository.save(card);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!creditCardRepository.existsById(id)) {
            throw new EntityNotFoundException("Credit card with id " + id + " not found");
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
        throw new IllegalStateException("User not authenticated");
    }

    private void validateTransaction(CreditCard card, Double amount) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new IllegalArgumentException("Card with id " + card.getId() + " is not active");
        }
        if (card.getLimitDate() != null && LocalDateTime.now().isBefore(card.getLimitDate()) && card.getLimit() < amount) {
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