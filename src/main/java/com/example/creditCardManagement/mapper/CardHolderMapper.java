package com.example.creditCardManagement.mapper;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.model.request.UpsertCardHolderRequest;
import com.example.creditCardManagement.model.response.CardHolderListResponse;
import com.example.creditCardManagement.model.response.CardHolderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardHolderMapper {
    CardHolder requestToCardHolder(UpsertCardHolderRequest request);

    @Mapping(source = "cardHolderId", target = "id")
    CardHolder requestToCardHolder(Long cardHolderId, UpsertCardHolderRequest request);

    CardHolderResponse cardHolderToResponse(CardHolder cardHolder);

    default CardHolderListResponse cardHoldersToCardHolderListResponse(List<CardHolder> cardHolders) {
        CardHolderListResponse response = new CardHolderListResponse();
        response.setCardHolders(cardHolders.stream().map(this::cardHolderToResponse).collect(Collectors.toList()));

        return response;
    }
}
