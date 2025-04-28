package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
@Schema(description = "Запрос для выполнения транзакции между картами")
public class UpsertTransactionRequest {
    @Schema(description = "Идентификатор исходной карты", example = "1")
    private Long fromId;

    @Schema(description = "Идентификатор целевой карты", example = "2")
    private Long toId;

    @Schema(description = "Сумма транзакции", example = "200.0")
    private Double amount;
}