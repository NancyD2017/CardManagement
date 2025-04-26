package com.example.creditCardManagement.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreditCardListResponse {
    private List<CreditCardResponse> creditCards = new ArrayList<>();
}
