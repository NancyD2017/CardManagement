package com.example.task_management.filter;

import com.example.task_management.model.entity.Task;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {
    public static Specification<Task> withFilter(TaskFilter filter) {
        return Specification.where(byAuthorId(filter.getAuthorId()))
                .and(byAssigneeId(filter.getAssigneeId()));
    }

    static Specification<Task> byAuthorId(Long authorId) {
        return (root, query, cb) -> {
            if (authorId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("author").get("id"), authorId);
        };
    }

    static Specification<Task> byAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            if (assigneeId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }
}
