package com.example.TaskManagement.model.response;

import com.example.TaskManagement.model.entity.Priority;
import com.example.TaskManagement.model.entity.TaskStatus;
import com.example.TaskManagement.model.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private List<String> comments;

    private User author;
    private User assignee;
}
