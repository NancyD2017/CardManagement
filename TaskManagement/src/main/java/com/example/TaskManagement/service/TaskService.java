package com.example.TaskManagement.service;

import com.example.TaskManagement.mapper.UserMapper;
import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.repository.UserRepository;
import com.example.TaskManagement.utils.BeanUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(MessageFormat.format(
                "Task with ID {0} not found!", id
        )));
    }

    public Task save(Task task, UpsertTaskRequest request) {
        setAuthorAndAssignee(task, request);
        return taskRepository.save(task);
    }

    public Task update(Task task, UpsertTaskRequest request) {
        Task existedTask = findById(task.getId());
        BeanUtils.copyNonNullProperties(task, existedTask);
        setAuthorAndAssignee(task, request);
        return taskRepository.save(task);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    private void setAuthorAndAssignee(Task task, UpsertTaskRequest request) {
        if (request.getAuthorId() != null) {
            try {
                Long authorId = Long.parseLong(request.getAuthorId());
                task.setAuthor(userRepository.findById(authorId)
                        .orElseThrow(() -> new IllegalArgumentException("Author with ID " + authorId + " not found")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid authorId format: " + request.getAuthorId());
            }
        }
        if (request.getAssigneeId() != null) {
            try {
                Long assigneeId = Long.parseLong(request.getAssigneeId());
                task.setAssignee(userRepository.findById(assigneeId)
                        .orElseThrow(() -> new IllegalArgumentException("Assignee with ID " + assigneeId + " not found")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid assigneeId format: " + request.getAssigneeId());
            }
        }
    }
}
