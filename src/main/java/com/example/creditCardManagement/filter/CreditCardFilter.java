package com.example.creditCardManagement.filter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
public class CreditCardFilter {
    private Integer pageSize = 10;
    private Integer pageNumber = 0;
    private Long cardHolderId;
    private String transactionHistory;

    public Pageable toPageable() {
        return PageRequest.of(pageNumber, pageSize);
    }
}
