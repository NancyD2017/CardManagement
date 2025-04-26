package com.example.creditCardManagement.controller;

import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.repository.CardHolderRepository;
import com.example.creditCardManagement.service.CardHolderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardHolderControllerTest {
    @Mock
    private CardHolderRepository cardHolderRepository;
    @InjectMocks
    private CardHolderService cardHolderService;

    private CardHolder cardHolder;

    @BeforeEach
    void setUp() {
        cardHolder = new CardHolder();
        cardHolder.setId(1L);
        cardHolder.setEmail("test@example.com");
    }

    @Test
    void findAll_success() {
        when(cardHolderRepository.findAll()).thenReturn(List.of(cardHolder));

        List<CardHolder> result = cardHolderService.findAll();

        assertEquals(1, result.size());
        assertEquals(cardHolder, result.get(0));
        verify(cardHolderRepository).findAll();
    }

    @Test
    void findById_success() {
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.of(cardHolder));

        CardHolder result = cardHolderService.findById(1L);

        assertEquals(cardHolder, result);
        verify(cardHolderRepository).findById(1L);
    }

    @Test
    void findById_notFound_throwsEntityNotFoundException() {
        when(cardHolderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cardHolderService.findById(1L));
        verify(cardHolderRepository).findById(1L);
    }

    @Test
    void deleteById_success() {
        when(cardHolderRepository.existsById(1L)).thenReturn(true);

        cardHolderService.deleteById(1L);

        verify(cardHolderRepository).deleteById(1L);
    }

    @Test
    void deleteById_notFound_throwsEntityNotFoundException() {
        when(cardHolderRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> cardHolderService.deleteById(1L));
        verify(cardHolderRepository, never()).deleteById(anyLong());
    }
}