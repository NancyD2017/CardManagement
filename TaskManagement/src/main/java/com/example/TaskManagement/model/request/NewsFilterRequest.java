package com.example.TaskManagement.model.request;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class NewsFilterRequest {
    @Nullable
    private String category;
}
