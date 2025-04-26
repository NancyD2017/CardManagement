package com.example.creditCardManagement.mapper;

import com.example.creditCardManagement.model.entity.CreditCard;
import com.example.creditCardManagement.model.request.UpsertCreditCardRequest;
import com.example.creditCardManagement.model.response.CreditCardListResponse;
import com.example.creditCardManagement.model.response.CreditCardResponse;
import com.example.creditCardManagement.model.response.CreditCardTransactionHistoryListResponse;
import com.example.creditCardManagement.model.response.CreditCardTransactionHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreditCardMapper {
    @Mapping(target = "cardHolder", ignore = true)
    CreditCard requestToCreditCard(UpsertCreditCardRequest request);

    CreditCardResponse creditCardToResponse(CreditCard creditCard);

    List<CreditCardResponse> creditCardListToCreditCardResponseList(List<CreditCard> creditCards);

    CreditCardTransactionHistoryResponse creditCardToCreditCardTransactionHistoryResponse(CreditCard creditCard);

    default CreditCardListResponse toCreditCardListResponse(List<CreditCard> creditCards) {
        CreditCardListResponse response = new CreditCardListResponse();
        response.setCreditCards(creditCardListToCreditCardResponseList(creditCards));
        return response;
    }

    default CreditCardTransactionHistoryListResponse toCreditCardTransactionHistoryListResponse(List<CreditCard> creditCards) {
        CreditCardTransactionHistoryListResponse response = new CreditCardTransactionHistoryListResponse();
        response.setCreditCards(creditCards.stream().map(this::creditCardToCreditCardTransactionHistoryResponse).toList());
        return response;
    }
}
