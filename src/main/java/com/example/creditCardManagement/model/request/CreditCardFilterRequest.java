package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardFilterValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
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
}
