package com.example.task_management.model.request;

import com.example.task_management.model.entity.Priority;
import com.example.task_management.model.entity.TaskStatus;
import com.example.task_management.validation.TaskRequestValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TaskRequestValid
public class UpsertTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Long authorId;
    private Long assigneeId;
}
