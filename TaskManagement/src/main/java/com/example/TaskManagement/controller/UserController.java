package com.example.TaskManagement.controller;

import com.example.TaskManagement.mapper.UserMapper;
import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.model.request.UpsertUserRequest;
import com.example.TaskManagement.model.response.UserListResponse;
import com.example.TaskManagement.model.response.UserResponse;
import com.example.TaskManagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taskManagement/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    //TODO
    //@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_USER')")
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
    public ResponseEntity<UserResponse> createUser(@RequestBody UpsertUserRequest user){
        return ResponseEntity.ok(userMapper.userToResponse(userService.save(userMapper.requestToUser(user))));
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


