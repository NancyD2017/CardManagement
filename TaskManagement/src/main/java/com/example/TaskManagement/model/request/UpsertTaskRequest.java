package com.example.TaskManagement.model.request;

import com.example.TaskManagement.model.entity.Priority;
import com.example.TaskManagement.model.entity.TaskStatus;
import com.example.TaskManagement.validation.TaskRequestValid;
import lombok.*;

@Data
@TaskRequestValid
public class UpsertTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;
}
