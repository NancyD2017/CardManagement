package com.example.task_management.service;

import com.example.task_management.model.entity.User;
import com.example.task_management.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public boolean deleteById(Long id) {
        if (!userRepository.findById(id).isPresent()) throw new EntityNotFoundException();
        userRepository.deleteById(id);
        return true;
    }
}
