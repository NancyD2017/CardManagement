package com.example.task_management.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> authoredTasks;

    @OneToMany(mappedBy = "assignee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> assignedTasks;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role_name", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void addAssignedTask(Task t) {
        if (assignedTasks == null) {
            assignedTasks = new ArrayList<>();
        }
        assignedTasks.add(t);
    }

    public void addAuthoredTask(Task t) {
        if (authoredTasks == null) {
            authoredTasks = new ArrayList<>();
        }
        authoredTasks.add(t);
    }

    public User(Long id, String username, String email, String password, List<Task> authoredTasks, List<Task> assignedTasks, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authoredTasks = authoredTasks;
        this.assignedTasks = assignedTasks;
        this.roles = roles;
    }

    public User() {

    }
}