package com.example.TaskManagement.mapper;

import com.example.TaskManagement.model.entity.Task;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.model.response.TaskListResponse;
import com.example.TaskManagement.model.response.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task requestToTask(UpsertTaskRequest request);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(source = "taskId", target = "id")
    Task requestToTask(Long taskId, UpsertTaskRequest request);

    TaskResponse taskToResponse(Task task);

    default TaskListResponse taskListToTaskResponseList(List<Task> tasks) {
        TaskListResponse response = new TaskListResponse();
        response.setTasks(tasks.stream().map(this::taskToResponse).collect(Collectors.toList()));

        return response;
    }
}
