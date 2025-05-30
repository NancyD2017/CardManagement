package com.example.creditCardManagement.repository;

import com.example.creditCardManagement.model.entity.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long>, JpaSpecificationExecutor<CreditCard> {
    Page<CreditCard> findAll(Pageable pageable);

    Optional<CreditCard> findByNumber(Long number);

    List<CreditCard> findByCardHolderId(Long cardHolderId);

    default Page<CreditCard> findAllBySpecification(Specification<CreditCard> specification, Pageable pageable) {
        return findAll(specification, pageable);
    }
}
