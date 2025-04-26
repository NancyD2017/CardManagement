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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;
    private final CardHolderRepository cardHolderRepository;

    public List<CreditCard> filterBy(CreditCardFilterRequest filter) {
        CreditCardFilter creditCardFilter = new CreditCardFilter();
        creditCardFilter.setCardHolderId(filter.getCardHolderId());
        creditCardFilter.setTransactionHistory(filter.getTransactionHistory());
        creditCardFilter.setPageNumber(filter.getPageNumber() != null ? filter.getPageNumber() : 0);
        creditCardFilter.setPageSize(filter.getPageSize() != null ? filter.getPageSize() : 10);

        Specification<CreditCard> specification = CreditCardSpecification.withFilter(creditCardFilter);
        return creditCardRepository.findAll(specification, creditCardFilter.toPageable());
    }

    public List<CreditCard> findAllByCardHolder() {
        return creditCardRepository.findByCardHolderId(findCardHolderId());
    }

    public CreditCard save(CreditCard creditCard, UpsertCreditCardRequest request) {
        if (creditCardRepository.findByNumber(creditCard.getNumber()).isPresent())
            throw new IllegalArgumentException("CreditCard with number " + creditCard.getNumber() + " already exists!");
        setCardHolder(creditCard, request);
        if (creditCard.getCardHolder() == null) throw new IllegalArgumentException("Wrong cardHolderId");
        creditCard.setDueToDate(LocalDateTime.now().plusYears(4));
        creditCard.setBalance(0.0D);
        CreditCard t = creditCardRepository.save(creditCard);
        updateCreditCards(t);
        return t;
    }

    public List<CreditCard> commitTransaction(Long fromId, Long toId, Double amount) {
        CreditCard existedFromCreditCard = creditCardRepository.findById(fromId).orElse(null);
        if (existedFromCreditCard == null)
            throw new IllegalArgumentException("CreditCard with id " + fromId + " doesn't exist!");
        CreditCard existedToCreditCard = creditCardRepository.findById(fromId).orElse(null);
        if (existedToCreditCard == null)
            throw new IllegalArgumentException("CreditCard with id " + toId + " doesn't exist!");

        if (LocalDateTime.now().isBefore(existedFromCreditCard.getLimitDate()) &&
                existedFromCreditCard.getLimit() < amount)
            throw new IllegalArgumentException("Amount is over limit for card " + fromId);

        if (existedFromCreditCard.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient funds for card " + fromId);

        boolean b1 = LocalDateTime.now().isAfter(existedFromCreditCard.getDueToDate());
        boolean b2 = LocalDateTime.now().isAfter(existedToCreditCard.getDueToDate());
        if (b1 || b2) {
            if (b1) existedFromCreditCard.setStatus(CardStatus.EXPIRED);
            if (b2) existedToCreditCard.setStatus(CardStatus.EXPIRED);
            CreditCard from = creditCardRepository.save(existedFromCreditCard);
            updateCreditCards(from);
            CreditCard to = creditCardRepository.save(existedFromCreditCard);
            updateCreditCards(to);
            throw new IllegalArgumentException("One or both of your cards are expired");
        }

        if (!existedFromCreditCard.getCardHolder().getId().equals(existedToCreditCard.getCardHolder().getId()) ||
                !existedFromCreditCard.getCardHolder().getId().equals(findCardHolderId()))
            throw new IllegalArgumentException("One or both of your cards are not yours");

        existedFromCreditCard.setBalance(existedFromCreditCard.getBalance() - amount);
        existedFromCreditCard.addTransaction(LocalDateTime.now() + ": Transfer to card " + toId + " " + amount + " roubles");
        existedToCreditCard.setBalance(existedToCreditCard.getBalance() + amount);
        existedToCreditCard.addTransaction(LocalDateTime.now() + ": Transfer from card " + fromId + " " + amount + " roubles");


        CreditCard from = creditCardRepository.save(existedFromCreditCard);
        updateCreditCards(from);
        CreditCard to = creditCardRepository.save(existedFromCreditCard);
        updateCreditCards(to);
        return List.of(from, to);
    }

    public CreditCard withdrawMoney(Long fromId, Double amount) {
        CreditCard existedFromCreditCard = creditCardRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("CreditCard with id " + fromId + " doesn't exist!"));

        if (LocalDateTime.now().isBefore(existedFromCreditCard.getLimitDate()) &&
                existedFromCreditCard.getLimit() < amount)
            throw new IllegalArgumentException("Amount is over limit for card " + fromId);

        if (existedFromCreditCard.getBalance() < amount)
            throw new IllegalArgumentException("Insufficient funds for card " + fromId);

        if (LocalDateTime.now().isAfter(existedFromCreditCard.getDueToDate())) {
            existedFromCreditCard.setStatus(CardStatus.EXPIRED);
            throw new IllegalArgumentException("Your card is expired " + fromId);
        }

        if (!existedFromCreditCard.getCardHolder().getId().equals(findCardHolderId()))
            throw new IllegalArgumentException("This card is not yours " + fromId);

        existedFromCreditCard.setBalance(existedFromCreditCard.getBalance() - amount);
        existedFromCreditCard.addTransaction(LocalDateTime.now() + ": Withdrawal " + amount + " roubles");


        CreditCard from = creditCardRepository.save(existedFromCreditCard);
        updateCreditCards(from);
        return from;
    }

    public CreditCard blockCreditCard(Long id) {
        CreditCard existedCreditCard = creditCardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        existedCreditCard.setStatus(CardStatus.BLOCKED);
        return creditCardRepository.save(existedCreditCard);
    }

    public CreditCard activateCreditCard(Long id) {
        CreditCard existedCreditCard = creditCardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        existedCreditCard.setStatus(CardStatus.ACTIVE);
        return creditCardRepository.save(existedCreditCard);
    }

    public CreditCard addLimit(Long id, Double limit, LimitDuration limitDuration) {
        CreditCard existedCreditCard = creditCardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        existedCreditCard.setLimit(limit);
        switch (limitDuration) {
            case YEAR -> existedCreditCard.setLimitDate(LocalDate.EPOCH.atStartOfDay().plusYears(1));
            case MONTH -> existedCreditCard.setLimitDate(LocalDate.EPOCH.atStartOfDay().plusMonths(1));
            case SEASON -> existedCreditCard.setLimitDate(LocalDate.EPOCH.atStartOfDay().plusMonths(3));
            case DAY -> existedCreditCard.setLimitDate(LocalDate.EPOCH.atStartOfDay().plusDays(1));
            default -> throw new IllegalArgumentException("Invalid limitDuration: Must be YEAR, MONTH OR SEASON");
        }
        existedCreditCard.addTransaction(LocalDateTime.now() + ": Added limit to the credit card: " + limit + " per " + limitDuration.toString().toLowerCase());
        return creditCardRepository.save(existedCreditCard);
    }

    public CreditCard requestToBlock(Long id) {
        CreditCard existedCreditCard = creditCardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        existedCreditCard.setStatus(CardStatus.REQUESTED_TO_BLOCK);
        existedCreditCard.addTransaction(LocalDateTime.now() + ": Request to block the credit card");
        return creditCardRepository.save(existedCreditCard);
    }

    private void setCardHolder(CreditCard creditCard, UpsertCreditCardRequest request) {
        if (request.getCardHolderId() != null) {
            Long cardHolderId = request.getCardHolderId();
            creditCard.setCardHolder(cardHolderRepository.findById(cardHolderId).orElse(null));
        }
    }

    private void updateCreditCards(CreditCard creditCard) {
        creditCard.getCardHolder().addCard(creditCard);
    }

    public void deleteById(Long id) {
        creditCardRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        creditCardRepository.deleteById(id);
    }

    private Long findCardHolderId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null &&
                authentication.getPrincipal() instanceof UserDetails userDetails &&
                userDetails instanceof com.example.creditCardManagement.security.AppUserDetails appUserDetails) {
            return appUserDetails.getId();
        }
        return null;
    }
}
