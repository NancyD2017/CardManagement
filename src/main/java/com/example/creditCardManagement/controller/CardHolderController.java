package com.example.creditCardManagement.controller;

import com.example.creditCardManagement.mapper.CardHolderMapper;
import com.example.creditCardManagement.model.entity.CardHolder;
import com.example.creditCardManagement.model.request.LoginRequest;
import com.example.creditCardManagement.model.request.RefreshTokenRequest;
import com.example.creditCardManagement.model.request.UpsertCardHolderRequest;
import com.example.creditCardManagement.model.response.AuthResponse;
import com.example.creditCardManagement.model.response.CardHolderListResponse;
import com.example.creditCardManagement.model.response.CardHolderResponse;
import com.example.creditCardManagement.model.response.RefreshTokenResponse;
import com.example.creditCardManagement.security.SecurityService;
import com.example.creditCardManagement.service.CardHolderService;
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

@RestController
@RequestMapping("/creditCardManagement/cardHolder")
@RequiredArgsConstructor
@Tag(name = "Владельцы карт", description = "Контроллер для управления владельца картами")
public class CardHolderController {
    private final CardHolderService cardHolderService;
    private final CardHolderMapper cardHolderMapper;
    private final SecurityService securityService;

    @Operation(summary = "Аутентификация владельца карты", description = "Аутентифицирует владельца карты и возвращает JWT токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> authCardHolder(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }


    @Operation(summary = "Получить всех владельцев карт", description = "Возвращает список всех владельцев карт")
    @ApiResponse(responseCode = "200", description = "Список владельцев карт")
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardHolderListResponse> getAllCardHolders() {
        return ResponseEntity.ok(cardHolderMapper.cardHoldersToCardHolderListResponse(cardHolderService.findAll()));
    }


    @Operation(summary = "Получить владельца карты по ID", description = "Возвращает данные владельца карты по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "404", description = "Владелец карты не найден")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardHolderResponse> getById(@PathVariable Long id) {
        CardHolder u = cardHolderService.findById(id);
        return ResponseEntity.ok(cardHolderMapper.cardHolderToResponse(u));
    }


    @Operation(summary = "Создать владельца карты", description = "Добавляет нового владельца карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Владелец карты создан"),
            @ApiResponse(responseCode = "400", description = "Владелец карты с таким email уже существует")
    })
    @PostMapping
    public ResponseEntity<?> createCardHolder(@Valid @RequestBody UpsertCardHolderRequest cardHolder) {
        CardHolder u = securityService.register(cardHolder);
        return ResponseEntity.ok(cardHolderMapper.cardHolderToResponse(u));
    }


    @Operation(summary = "Обновить токен владельца карты", description = "Обновляет токен владельца карты по истечению срока его действия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Токен обновлен"),
            @ApiResponse(responseCode = "404", description = "Ошибка при попытке обновить токен")
    })
    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse rtr = securityService.refreshToken(request);
        return ResponseEntity.ok().body(rtr);
    }


    @Operation(summary = "Удалить владельца карты", description = "Удаляет владельца карты по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Владелец карты удален"),
            @ApiResponse(responseCode = "404", description = "Владелец карты не найден")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCardHolder(@PathVariable Long id) {
        cardHolderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CardHolderResponse> handleNotFoundException(Exception ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}


