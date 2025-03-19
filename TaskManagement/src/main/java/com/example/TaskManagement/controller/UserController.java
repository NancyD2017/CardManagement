package com.example.TaskManagement.controller;

import com.example.TaskManagement.mapper.UserMapper;
import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.model.request.LoginRequest;
import com.example.TaskManagement.model.request.RefreshTokenRequest;
import com.example.TaskManagement.model.request.UpsertUserRequest;
import com.example.TaskManagement.model.response.AuthResponse;
import com.example.TaskManagement.model.response.RefreshTokenResponse;
import com.example.TaskManagement.model.response.UserListResponse;
import com.example.TaskManagement.model.response.UserResponse;
import com.example.TaskManagement.repository.UserRepository;
import com.example.TaskManagement.security.SecurityService;
import com.example.TaskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taskManagement/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityService securityService;

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> authUser(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.authenticateUser(loginRequest));
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getAllUsers(){
        return ResponseEntity.ok(userMapper.userListToUserResponseList(userService.findAll()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id){
        return userService.findById(id) != null
                ? ResponseEntity.ok(userMapper.userToResponse(userService.findById(id)))
                : ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UpsertUserRequest user){
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        return ResponseEntity.ok(userMapper.userToResponse(securityService.register(user)));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UpsertUserRequest user){
        User u = userService.update(userMapper.requestToUser(id, user));
        return u != null
                ? ResponseEntity.ok(userMapper.userToResponse(u))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


