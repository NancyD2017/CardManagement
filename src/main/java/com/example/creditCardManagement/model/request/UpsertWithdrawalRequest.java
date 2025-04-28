package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.validation.CreditCardRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Запрос для списания средств с карты")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
public class UpsertWithdrawalRequest {
    @Schema(description = "Идентификатор карты", example = "1")
    private Long fromId;

    @Schema(description = "Сумма списания", example = "200.0")
    private Double amount;
}