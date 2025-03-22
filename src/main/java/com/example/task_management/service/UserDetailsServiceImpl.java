package com.example.task_management.service;

import com.example.task_management.model.entity.User;
import com.example.task_management.repository.UserRepository;
import com.example.task_management.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws EntityNotFoundException {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found. Email is: " + email));
        return new AppUserDetails(user);
    }
}
