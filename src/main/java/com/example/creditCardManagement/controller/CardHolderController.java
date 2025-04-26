package com.example.creditCardManagement.controller;

import com.example.creditCardManagement.mapper.CardHolderMapper;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/creditCardManagement/cardHolder")
@RequiredArgsConstructor
@Tag(name = "Владельцы карт", description = "Контроллер для управления владельцами карт")
public class CardHolderController {
    private final CardHolderService cardHolderService;
    private final CardHolderMapper cardHolderMapper;
    private final SecurityService securityService;

    @Operation(summary = "Аутентификация владельца", description = "Аутентифицирует владельца и возвращает JWT токен")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(securityService.authenticateUser(request));
    }

    @Operation(summary = "Получить всех владельцев", description = "Возвращает список всех владельцев карт")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список владельцев возвращён"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardHolderListResponse> getAll() {
        return ResponseEntity.ok(cardHolderMapper.cardHoldersToCardHolderListResponse(cardHolderService.findAll()));
    }

    @Operation(summary = "Получить владельца по ID", description = "Возвращает владельца по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Владелец найден"),
            @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardHolderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cardHolderMapper.cardHolderToResponse(cardHolderService.findById(id)));
    }

    @Operation(summary = "Создать владельца", description = "Регистрирует нового владельца карты")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Владелец создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<CardHolderResponse> create(@Valid @RequestBody UpsertCardHolderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardHolderMapper.cardHolderToResponse(securityService.register(request)));
    }

    @Operation(summary = "Обновить токен", description = "Обновляет JWT токен")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @Operation(summary = "Удалить владельца", description = "Удаляет владельца по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Владелец удалён"),
            @ApiResponse(responseCode = "404", description = "Владелец не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardHolderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleNotFound() {
        return ResponseEntity.notFound().build();
    }
}