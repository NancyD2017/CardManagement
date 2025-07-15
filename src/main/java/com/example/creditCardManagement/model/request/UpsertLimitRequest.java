package com.example.creditCardManagement.model.request;

import com.example.creditCardManagement.model.entity.LimitDuration;
import com.example.creditCardManagement.validation.CreditCardRequestValid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Запрос для установки лимита на карту")
@AllArgsConstructor
@NoArgsConstructor
@CreditCardRequestValid
public class UpsertLimitRequest {
    @Schema(description = "Период действия лимита", example = "MONTH")
    private LimitDuration limitDuration;

    @Schema(description = "Сумма лимита", example = "500.0")
    private Double limit;

}