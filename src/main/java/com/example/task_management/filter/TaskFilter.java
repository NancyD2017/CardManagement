package com.example.task_management.filter;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Data
@Getter
@Setter
@NoArgsConstructor
public class TaskFilter {
    private Integer pageSize = 10;
    private Integer pageNumber = 0;
    private Long authorId;
    private Long assigneeId;

    public Pageable toPageable() {
        return PageRequest.of(pageNumber, pageSize);
    }
}
