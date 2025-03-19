package com.example.TaskManagement.controller;

import com.example.TaskManagement.mapper.TaskMapper;
import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.model.response.TaskListResponse;
import com.example.TaskManagement.model.response.TaskResponse;
import com.example.TaskManagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taskManagement/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskListResponse> getAllTasks() {
        return ResponseEntity.ok(taskMapper.taskListToTaskResponseList(taskService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<TaskResponse> getById(@PathVariable Long id) {
        return taskService.findById(id) != null
                ? ResponseEntity.ok(taskMapper.taskToResponse(taskService.findById(id)))
                : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody UpsertTaskRequest task) {
        return ResponseEntity.ok(taskMapper.taskToResponse(taskService.save(taskMapper.requestToTask(task), task)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody UpsertTaskRequest task) {
        Task t = taskService.update(taskMapper.requestToTask(id, task), task);
        return t != null
                ? ResponseEntity.ok(taskMapper.taskToResponse(t))
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


