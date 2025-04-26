package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.repository.CardHolderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardHolderService {
    private final CardHolderRepository cardHolderRepository;

    public List<CardHolder> findAll() {
        return cardHolderRepository.findAll();
    }

    public CardHolder findById(Long id) {
        return cardHolderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteById(Long id) {
        if (cardHolderRepository.findById(id).isEmpty()) throw new EntityNotFoundException();
        cardHolderRepository.deleteById(id);
    }
}
