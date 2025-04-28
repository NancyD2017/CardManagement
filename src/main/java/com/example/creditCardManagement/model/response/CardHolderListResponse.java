package com.example.creditCardManagement.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Ответ со списком владельцев карт")
@Data
public class CardHolderListResponse {
    @Schema(description = "Список владельцев карт")
    private List<CardHolderResponse> cardHolders = new ArrayList<>();
}