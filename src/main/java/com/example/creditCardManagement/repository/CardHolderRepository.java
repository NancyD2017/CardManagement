package com.example.creditCardManagement.repository;

import com.example.creditCardManagement.model.entity.CardHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {
    Optional<CardHolder> findByEmail(String email);

    Page<CardHolder> findAll(Pageable pageable);
}
