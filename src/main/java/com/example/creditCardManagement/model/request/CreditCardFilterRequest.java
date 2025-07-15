package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardFilterValid;
import io.swagger.v3.oas.annotations.media.Schema;

@CreditCardFilterValid
@Schema(description = "Запрос для фильтрации кредитных карт")
public class CreditCardFilterRequest {
    @Schema(description = "Идентификатор владельца карты для фильтрации", example = "1")
    private Long cardHolderId;

    @Schema(description = "Номер страницы для пагинации", example = "0")
    private Integer pageNumber;

    @Schema(description = "Размер страницы для пагинации", example = "10")
    private Integer pageSize;

    @Schema(description = "История транзакций", example = "2025-04-26T00:00:00 : Added limit 25000.0 per month")
    private String transactionHistory;

    public Long getCardHolderId() {
        return cardHolderId;
    }

    public void setCardHolderId(Long cardHolderId) {
        this.cardHolderId = cardHolderId;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(String transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
