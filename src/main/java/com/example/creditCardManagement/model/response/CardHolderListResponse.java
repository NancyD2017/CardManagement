package com.example.creditCardManagement.model.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CardHolderListResponse {
    private List<CardHolderResponse> cardHolders = new ArrayList<>();
}
