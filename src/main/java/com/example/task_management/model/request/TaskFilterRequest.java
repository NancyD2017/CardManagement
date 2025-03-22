package com.example.task_management.model.request;

import com.example.task_management.validation.TaskFilterValid;
import lombok.Data;

@Data
@TaskFilterValid
public class TaskFilterRequest {
    private Long authorId;
    private Long assigneeId;
    private Integer pageSize = 10;
    private Integer pageNumber = 0;
}
