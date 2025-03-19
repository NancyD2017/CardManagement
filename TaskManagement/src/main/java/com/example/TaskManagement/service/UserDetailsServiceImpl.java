package com.example.TaskManagement.service;

import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.repository.UserRepository;
import com.example.TaskManagement.security.AppUserDetails;
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
    public UserDetails loadUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found. Username is: " + username));
        return new AppUserDetails(user);
    }
}
