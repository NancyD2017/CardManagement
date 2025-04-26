package com.example.creditCardManagement.service;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.repository.CardHolderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardHolderService {
    private final CardHolderRepository cardHolderRepository;

    public List<CardHolder> findAll() {
        return cardHolderRepository.findAll();
    }

    public CardHolder findById(Long id) {
        return cardHolderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CardHolder with id " + id + " not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!cardHolderRepository.existsById(id)) {
            throw new EntityNotFoundException("CardHolder with id " + id + " not found");
        }
        cardHolderRepository.deleteById(id);
    }
}