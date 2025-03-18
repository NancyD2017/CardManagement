package com.example.TaskManagement.model.request;

import com.example.TaskManagement.model.entity.Priority;
import com.example.TaskManagement.model.entity.TaskStatus;
import lombok.*;

@Data
public class UpsertTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private String authorId;
    private String assigneeId;
}
