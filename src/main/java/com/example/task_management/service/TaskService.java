package com.example.task_management.service;

import com.example.task_management.filter.TaskFilter;
import com.example.task_management.filter.TaskSpecification;
import com.example.task_management.mapper.TaskMapper;
import com.example.task_management.model.entity.Task;
import com.example.task_management.model.entity.TaskStatus;
import com.example.task_management.model.request.TaskFilterRequest;
import com.example.task_management.model.request.UpsertTaskRequest;
import com.example.task_management.repository.TaskRepository;
import com.example.task_management.repository.UserRepository;
import com.example.task_management.utils.BeanUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<Task> filterBy(@Valid TaskFilterRequest filter) {
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setAuthorId(filter.getAuthorId());
        taskFilter.setAssigneeId(filter.getAssigneeId());
        taskFilter.setPageNumber(filter.getPageNumber() != null ? filter.getPageNumber() : 0);
        taskFilter.setPageSize(filter.getPageSize() != null ? filter.getPageSize() : 10);

        Specification<Task> specification = TaskSpecification.withFilter(taskFilter);
        return taskRepository.findAll(specification, taskFilter.toPageable());
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Task save(Task task, UpsertTaskRequest request) {
        if (taskRepository.findByTitle(task.getTitle()).isPresent())
            throw new IllegalArgumentException("Task with title " + task.getTitle() + " already exists!");
        setAuthorAndAssignee(task, request);
        if (task.getAssignee() == null || task.getAuthor() == null)
            throw new IllegalArgumentException("Wrong authorId or assigneeId");
        Task t = taskRepository.save(task);
        updateTasks(t);
        return t;
    }

    public Task update(Long id, UpsertTaskRequest request) {
        Task existedTask = taskRepository.findById(id).orElse(null);
        if (existedTask == null) throw new IllegalArgumentException("Task with id " + id + " doesn't exist!");

        Task titleT = taskRepository.findByTitle(request.getTitle()).orElse(null);
        if (titleT != null && !titleT.equals(existedTask))
            throw new IllegalArgumentException("Task with title " + titleT + " already exists!");

        BeanUtils.copyNonNullProperties(taskMapper.requestToTask(request), existedTask);
        setAuthorAndAssignee(existedTask, request);

        if (existedTask.getAssignee() == null || existedTask.getAuthor() == null)
            throw new IllegalArgumentException("Wrong authorId or assigneeId");

        Task t = taskRepository.save(existedTask);
        updateTasks(t);
        return t;
    }

    public Task addComment(Long id, String comment) {
        Task existedTask = taskRepository.findById(id).orElse(null);
        if (existedTask == null) throw new EntityNotFoundException();
        existedTask.addComment(comment);
        return taskRepository.save(existedTask);
    }

    public Task changeStatus(Long id, String status) {
        Task existedTask = taskRepository.findById(id).orElse(null);
        if (existedTask == null) throw new EntityNotFoundException();
        try {
            TaskStatus t = TaskStatus.valueOf(status);
            existedTask.setStatus(t);
        } catch (Exception e) {
            throw new IllegalArgumentException("Wrong status");
        }
        return taskRepository.save(existedTask);
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

    private void updateTasks(Task task) {
        task.getAuthor().addAuthoredTask(task);
        task.getAssignee().addAssignedTask(task);
    }

    public boolean deleteById(Long id) {
        if (!taskRepository.findById(id).isPresent()) throw new EntityNotFoundException();
        taskRepository.deleteById(id);
        return true;
    }
}
