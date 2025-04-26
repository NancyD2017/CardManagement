package com.example.creditCardManagement.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_cards")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long number;//TODO
    private LocalDateTime dueToDate;
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private Double balance;

    @JsonFormat(pattern = "yyyy-MM-dd'T'00:00:00")
    @Column(name = "limit_date")
    private LocalDateTime limitDate;

    @Column(name = "credit_limit")
    private Double limit;

    @Column(name = "remaining_balance")
    private Double left;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "card_holder_id")
    private CardHolder cardHolder;
    @ElementCollection
    @Column(name = "transaction_history")
    private List<String> transactionHistory;

    public void addTransaction(String transaction) {
        if (transactionHistory == null) {
            transactionHistory = new ArrayList<>();
        }
        transactionHistory.add(transaction);
    }
}