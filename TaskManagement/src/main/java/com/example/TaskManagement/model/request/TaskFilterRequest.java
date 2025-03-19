package com.example.TaskManagement.model.request;

import lombok.Data;

@Data
public class TaskFilterRequest {
    private Long authorId;
    private Long assigneeId;
    private Integer pageSize = 10;
    private Integer pageNumber = 0;
}
