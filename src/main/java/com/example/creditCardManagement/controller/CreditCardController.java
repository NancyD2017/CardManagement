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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/creditCardManagement/creditCard")
@RequiredArgsConstructor
@Tag(name = "Банковские карты", description = "Контроллер для управления банковскими картами")
@SecurityRequirement(name = "bearerAuth")
public class CreditCardController {
    private final CreditCardService creditCardService;
    private final CreditCardMapper creditCardMapper;

    @Operation(summary = "Получить банковские карты пользователя", description = "Возвращает список карт текущего пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт возвращён"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardListResponse> getUserCards() {
        return ResponseEntity.ok(creditCardMapper.toCreditCardListResponse(creditCardService.findAllByCardHolder()));
    }

    @Operation(summary = "Фильтр банковских карт", description = "Фильтрует карты по владельцу и транзакциям с пагинацией")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список карт возвращён"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<CreditCardResponse>> filterCards(@Valid @RequestBody CreditCardFilterRequest filter) {
        return ResponseEntity.ok(creditCardMapper.creditCardPageToCreditCardResponsePage(creditCardService.filterBy(filter)));
    }

    @Operation(summary = "Создать банковскую карту", description = "Создаёт новую карту")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Карта создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> createCard(@Valid @RequestBody UpsertCreditCardRequest request) {
        CreditCard card = creditCardService.save(creditCardMapper.requestToCreditCard(request), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCardMapper.creditCardToResponse(card));
    }

    @Operation(summary = "Получить историю транзакций", description = "Возвращает историю транзакций карт пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "История транзакций возвращена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @GetMapping("/transactionHistory")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardTransactionHistoryListResponse> getTransactionHistory() {
        return ResponseEntity.ok(creditCardMapper.toCreditCardTransactionHistoryListResponse(creditCardService.findAllByCardHolder()));
    }

    @Operation(summary = "Перевод между картами", description = "Совершает перевод между картами пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод выполнен"),
            @ApiResponse(responseCode = "400", description = "Некорректная операция")
    })
    @PutMapping("/commitTransaction")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardListResponse> commitTransaction(@Valid @RequestBody UpsertTransactionRequest request) {
        return ResponseEntity.ok(creditCardMapper.toCreditCardListResponse(creditCardService.commitTransaction(
                request.getFromId(), request.getToId(), request.getAmount())));
    }

    @Operation(summary = "Снятие денег", description = "Снимает деньги с карты пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Снятие выполнено"),
            @ApiResponse(responseCode = "400", description = "Некорректная операция")
    })
    @PutMapping("/withdrawMoney")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardResponse> withdrawMoney(@Valid @RequestBody UpsertWithdrawalRequest request) {
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(creditCardService.withdrawMoney(
                request.getFromId(), request.getAmount())));
    }

    @Operation(summary = "Добавить лимит", description = "Устанавливает лимит для карты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Лимит установлен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PutMapping("/addLimit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> addLimit(@PathVariable Long id, @Valid @RequestBody UpsertLimitRequest request) {
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(creditCardService.addLimit(
                id, request.getLimit(), request.getLimitDuration())));
    }

    @Operation(summary = "Запрос на блокировку карты", description = "Создаёт запрос на блокировку карты")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос принят"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PutMapping("/requestToBlock/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<CreditCardResponse> requestToBlock(@PathVariable Long id) {
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(creditCardService.requestToBlock(id)));
    }

    @Operation(summary = "Блокировка карты", description = "Блокирует карту")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта заблокирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PutMapping("/block/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(creditCardService.blockCreditCard(id)));
    }

    @Operation(summary = "Активация карты", description = "Активирует карту")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карта активирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PutMapping("/activate/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponse> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(creditCardMapper.creditCardToResponse(creditCardService.activateCreditCard(id)));
    }

    @Operation(summary = "Удаление карты", description = "Удаляет карту")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Карта удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        creditCardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        return ResponseEntity.notFound().build();
    }
}