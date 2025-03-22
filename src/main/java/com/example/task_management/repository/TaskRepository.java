package com.example.task_management.repository;

import com.example.task_management.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAll(Pageable pageable);

    Optional<Task> findByTitle(String title);

    List<Task> findAll(Specification<Task> specification, Pageable pageable);
}
