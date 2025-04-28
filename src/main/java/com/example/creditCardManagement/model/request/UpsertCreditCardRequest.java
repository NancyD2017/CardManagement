package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.CardStatus;
import com.example.creditCardManagement.validation.CreditCardRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
@Schema(description = "Запрос для создания или обновления кредитной карты")
public class UpsertCreditCardRequest {
    @Schema(description = "Идентификатор владельца карты", example = "1")
    private Long cardHolderId;

    @Schema(description = "Номер карты", example = "123456789")
    private Long number;

    @Schema(description = "Начальный баланс карты", example = "1000.0")
    private Double balance;

    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus status;
}