package com.example.TaskManagement.service;

import com.example.TaskManagement.utils.BeanUtils;
import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(
                "User with ID {0} not found!", id)));
    }
    public User save(User user){
        return userRepository.save(user);
    }
    public User update(User user){
        User existedUser = findById(user.getId());
        if (user.getRoles() != null && existedUser.getRoles().contains("ROLE_USER")) {
            user.setRoles(existedUser .getRoles());
        }
        BeanUtils.copyNonNullProperties(user, existedUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public void deleteById(Long id){
        //TODO delete tasks
        userRepository.deleteById(id);
    }
}
