package com.example.TaskManagement.service;

import com.example.TaskManagement.filter.TaskFilter;
import com.example.TaskManagement.filter.TaskSpecification;
import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.entity.TaskStatus;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.repository.UserRepository;
import com.example.TaskManagement.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> filterBy(TaskFilter filter){
        Specification<Task> specification = TaskSpecification.withFilter(filter);
        return taskRepository.findAll(specification, filter.toPageable());
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task save(Task task, UpsertTaskRequest request) {
        setAuthorAndAssignee(task, request);
        if (task.getAssignee() == null || task.getAuthor() == null) return null;
        return taskRepository.save(task);
    }

    public Task update(Task task, UpsertTaskRequest request) {
        Task existedTask = findById(task.getId());
        BeanUtils.copyNonNullProperties(task, existedTask);
        setAuthorAndAssignee(task, request);
        if (task.getAssignee() == null || task.getAuthor() == null) return null;
        return taskRepository.save(task);
    }

    public Task addComment(Long id, String comment) {
        Task existedTask = findById(id);
        existedTask.addComment(comment);
        return taskRepository.save(existedTask);
    }
    public Task changeStatus(Long id, String status) {
        Task existedTask = findById(id);
        try {
            TaskStatus t = TaskStatus.valueOf(status);
            existedTask.setStatus(t);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return taskRepository.save(existedTask);
    }

    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    private void setAuthorAndAssignee(Task task, UpsertTaskRequest request) {
        if (request.getAuthorId() != null) {
            Long authorId = request.getAuthorId();
            task.setAuthor(userRepository.findById(authorId).orElse(null));
        }
        if (request.getAssigneeId() != null) {
            Long assigneeId = request.getAssigneeId();
            task.setAssignee(userRepository.findById(assigneeId).orElse(null));
        }
    }
}
