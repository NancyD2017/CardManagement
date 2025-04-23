package com.example.task_management.controller;

import com.example.task_management.mapper.UserMapper;
import com.example.task_management.model.entity.User;
import com.example.task_management.model.request.LoginRequest;
import com.example.task_management.model.request.RefreshTokenRequest;
import com.example.task_management.model.request.UpsertUserRequest;
import com.example.task_management.model.response.AuthResponse;
import com.example.task_management.model.response.RefreshTokenResponse;
import com.example.task_management.model.response.UserListResponse;
import com.example.task_management.model.response.UserResponse;
import com.example.task_management.security.SecurityService;
import com.example.task_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taskManagement/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Контроллер для управления пользователями")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityService securityService;

    @Operation(summary = "Аутентификация пользователя", description = "Аутентифицирует пользователя и возвращает JWT токен")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> authUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }


    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers() {
        return ResponseEntity.ok(userMapper.userListToUserResponseList(userService.findAll()));
    }


    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User u = userService.findById(id);
        return ResponseEntity.ok(userMapper.userToResponse(u));
    }


    @Operation(summary = "Создать пользователя", description = "Добавляет нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Пользователь с таким email уже существует")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UpsertUserRequest user) {
        User u = securityService.register(user);
        return ResponseEntity.ok(userMapper.userToResponse(u));
    }


    @Operation(summary = "Обновить токен пользователя", description = "Обновляет токен пользователя по истечению срока его действия")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Токен обновлен"),
            @ApiResponse(responseCode = "404", description = "Ошибка при попытке обновить токен")
    })
    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse rtr = securityService.refreshToken(request);
        return ResponseEntity.ok().body(rtr);
    }


    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<UserResponse> handleNotFoundException(Exception ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

}


