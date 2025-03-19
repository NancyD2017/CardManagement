package com.example.TaskManagement.controller;

import com.example.TaskManagement.filter.TaskFilter;
import com.example.TaskManagement.mapper.TaskMapper;
import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.request.TaskFilterRequest;
import com.example.TaskManagement.model.request.UpsertPutRequest;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.model.response.TaskListResponse;
import com.example.TaskManagement.model.response.TaskResponse;
import com.example.TaskManagement.service.TaskService;
import jakarta.validation.Valid;
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

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> filterBy(@Valid @RequestBody TaskFilterRequest filter) {
        TaskFilter taskFilter = new TaskFilter();

        taskFilter.setAuthorId(filter.getAuthorId());
        taskFilter.setAssigneeId(filter.getAssigneeId());
        taskFilter.setPageNumber(filter.getPageNumber() != null ? filter.getPageNumber() : 0);
        taskFilter.setPageSize(filter.getPageSize() != null ? filter.getPageSize() : 10);

        return ResponseEntity.ok(taskMapper.taskListToTaskResponseList(taskService.filterBy(taskFilter)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createTask(@Valid @RequestBody UpsertTaskRequest task) {
        Task t = taskService.save(taskMapper.requestToTask(task), task);
        return t != null
                ? ResponseEntity.ok(taskMapper.taskToResponse(t))
                : ResponseEntity.badRequest().body("Wrong authorId or assigneeId");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateTask(@Valid @PathVariable Long id, @RequestBody UpsertTaskRequest task) {
        Task t = taskService.update(taskMapper.requestToTask(id, task), task);
        return t != null
                ? ResponseEntity.ok(taskMapper.taskToResponse(t))
                : ResponseEntity.badRequest().body("Wrong authorId or assigneeId");
    }

    @PutMapping("/addComment/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<TaskResponse> addComment(@PathVariable Long id, @RequestBody UpsertPutRequest comment) {
        Task t = taskService.addComment(id, comment.getRequest());
        return ResponseEntity.ok(taskMapper.taskToResponse(t));
    }

    @PutMapping("/changeStatus/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody UpsertPutRequest status) {
        Task t = taskService.changeStatus(id, status.getRequest());
        return t != null
                ? ResponseEntity.ok(taskMapper.taskToResponse(t))
                : ResponseEntity.badRequest().body("Wrong status");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}


