package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Ответ со списком кредитных карт")
@Data
public class CreditCardListResponse {
    @Schema(description = "Список кредитных карт")
    private List<CreditCardResponse> creditCards = new ArrayList<>();
}