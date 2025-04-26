package com.example.creditCardManagement.controller;

import com.example.creditCardManagement.mapper.CreditCardMapper;
import com.example.creditCardManagement.model.entity.CreditCard;
import com.example.creditCardManagement.model.request.*;
import com.example.creditCardManagement.model.response.CreditCardListResponse;
import com.example.creditCardManagement.model.response.CreditCardResponse;
import com.example.creditCardManagement.model.response.CreditCardTransactionHistoryListResponse;
import com.example.creditCardManagement.service.CreditCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/creditCardManagement/creditCard")
@RequiredArgsConstructor
@Tag(name = "Банковские карты", description = "Контроллер для управления банковскими картами")
public class CreditCardController {
    private final CreditCardService creditCardService;
    private final CreditCardMapper creditCardMapper;


    @Operation(summary = "Получить банковские карты по ID пользователя", description = "Возвращает банковскую карту по идентификатору (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Банковские карты найдены"),
            @ApiResponse(responseCode = "404", description = "Банковские карты не найдены")
    })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardListResponse> getById() {
        List<CreditCard> list = creditCardService.findAllByCardHolder();
        return ResponseEntity.ok(creditCardMapper.toCreditCardListResponse(list));
    }


    @Operation(summary = "Фильтр банковских карт", description = "Фильтрует банковские карты по владельцу и транзакциям, а также проводит пагинацию")
    @ApiResponse(responseCode = "200", description = "Список банковских карт с фильтром")
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CreditCardResponse>> filterBy(@Valid @RequestBody CreditCardFilterRequest filter) {
        return ResponseEntity.ok(creditCardMapper.creditCardListToCreditCardResponseList(creditCardService.filterBy(filter)));
    }


    @Operation(summary = "Создать банковскую карту", description = "Добавляет новую банковскую карту (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Банковская карты создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> createCreditCard(@Valid @RequestBody UpsertCreditCardRequest creditCard) {
        CreditCard t = creditCardService.save(creditCardMapper.requestToCreditCard(creditCard), creditCard);
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(t));
    }

    @Operation(summary = "Просматривать историю транзакций по картам пользователя", description = "Возвращает историю транзакций по идентификатору пользователя")
    @ApiResponse(responseCode = "200", description = "История транзакций найдена")
    @GetMapping("/transactionHistory")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardTransactionHistoryListResponse> getTransactionHistoryById() {
        List<CreditCard> list = creditCardService.findAllByCardHolder();
        return ResponseEntity.ok(creditCardMapper.toCreditCardTransactionHistoryListResponse(list));
    }

    @Operation(summary = "Совершить перевод между банковскими картами одного пользователя", description = "Совершает перевод между банковскими картами одного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Перевод выполнен"),
            @ApiResponse(responseCode = "400", description = "Некорректная операция: неправильные номера карт, исчерпан лимит, недостаточно средств, карты просрочены или не принадлежат пользователю")
    })
    @PutMapping("commitTransaction")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardListResponse> commitTransaction(@Valid @RequestBody UpsertTransactionRequest request) {
        List<CreditCard> cards = creditCardService.commitTransaction(request.getFromId(), request.getToId(), request.getAmount());
        return ResponseEntity.ok(creditCardMapper.toCreditCardListResponse(cards));
    }

    @Operation(summary = "Совершить перевод между банковскими картами одного пользователя", description = "Совершает перевод между банковскими картами одного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Перевод выполнен"),
            @ApiResponse(responseCode = "400", description = "Некорректная операция: неправильный номер карты, исчерпан лимит, недостаточно средств, карта просрочена или не принадлежит пользователю")
    })
    @PutMapping("withdrawMoney")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardResponse> withdrawMoney(@Valid @RequestBody UpsertWithdrawalRequest request) {
        CreditCard card = creditCardService.withdrawMoney(request.getFromId(), request.getAmount());
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(card));
    }

    @Operation(summary = "Добавить лимит банковской карте", description = "Добавляет лимит банковской карте (админ)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лимит банковской карте добавлен"),
            @ApiResponse(responseCode = "404", description = "Банковская карта не найдена")
    })
    @PutMapping("/addLimit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> addLimit(@PathVariable Long id, @Valid @RequestBody UpsertLimitRequest limit) {
        CreditCard t = creditCardService.addLimit(id, limit.getLimit(), limit.getLimitDuration());
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(t));
    }

    @Operation(summary = "Запросить блокировку банковской карты", description = "Добавляет запрос на блокировку в историю транзакций карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос на блокировку принят"),
            @ApiResponse(responseCode = "404", description = "Банковская карта не найдена")
    })
    @PutMapping("/requestToBlock/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardResponse> requestToBlock(@PathVariable Long id) {
        CreditCard c = creditCardService.requestToBlock(id);
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(c));
    }


    @Operation(summary = "Заблокировать банковскую карту", description = "Блокирует банковскую карту (админ)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Статус банковской карты обновлен"),
            @ApiResponse(responseCode = "404", description = "Банковская карта не найдена")
    })
    @PutMapping("/blockCreditCard/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> blockCreditCard(@PathVariable Long id) {
        CreditCard t = creditCardService.blockCreditCard(id);
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(t));
    }

    @Operation(summary = "Активировать банковскую карту", description = "Активирует банковскую карту (админ)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Статус банковской карты обновлен"),
            @ApiResponse(responseCode = "404", description = "Банковская карта не найдена")
    })
    @PutMapping("/activateCreditCard/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> activateCreditCard(@PathVariable Long id) {
        CreditCard t = creditCardService.activateCreditCard(id);
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(t));
    }


    @Operation(summary = "Удалить банковскую карту", description = "Удаляет банковскую карту по ID (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Банковская карта удалена"),
            @ApiResponse(responseCode = "404", description = "Банковская карта не найдена")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable Long id) {
        creditCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CreditCardResponse> handleEntityNotFound(Exception ex) {
        return ResponseEntity.notFound().build();
    }
}


