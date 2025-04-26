package com.example.creditCardManagement.filter;

import com.example.creditCardManagement.model.entity.CreditCard;
import org.springframework.data.jpa.domain.Specification;

public class CreditCardSpecification {
    public static Specification<CreditCard> withFilter(CreditCardFilter filter) {
        return Specification.where(byCardHolderId(filter.getCardHolderId()))
                .and(byTransactionHistory(filter.getTransactionHistory()));
    }

    static Specification<CreditCard> byCardHolderId(Long cardHolderId) {
        return (root, query, cb) -> {
            if (cardHolderId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("cardHolder").get("id"), cardHolderId);
        };
    }

    static Specification<CreditCard> byTransactionHistory(String transactionHistory) {
        return (root, query, cb) -> {
            if (transactionHistory == null || transactionHistory.trim().isEmpty()) {
                return cb.conjunction();
            }
            var transactionJoin = root.join("transactionHistory");
            return cb.like(cb.lower(transactionJoin.as(String.class)), "%" + transactionHistory.toLowerCase() + "%");
        };
    }
}