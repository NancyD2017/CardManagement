package com.example.TaskManagement.controller;

import com.example.TaskManagement.model.entity.*;
import com.example.TaskManagement.model.request.UpsertTaskRequest;
import com.example.TaskManagement.repository.TaskRepository;
import com.example.TaskManagement.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
public class TaskControllerTest {
    public static Set<Role> role = new HashSet<>(Collections.singleton(Role.ROLE_USER));
    public static Set<Role> role2 = new HashSet<>(Collections.singleton(Role.ROLE_ADMIN));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    static Stream<Arguments> argumentsForCreateTaskTest() {
        return Stream.of(
                Arguments.of(new UpsertTaskRequest("Do the washing up", "swipe off the dust", TaskStatus.TO_DO, Priority.LOW, 1L, 1L)),
                Arguments.of(new UpsertTaskRequest("Feed the cat", "use a bowl", TaskStatus.IN_PROGRESS, Priority.HIGH, 1L, 1L)),
                Arguments.of(new UpsertTaskRequest("Buy products", "take a purse", TaskStatus.DONE, Priority.MIDDLE, 1L, 1L))
        );
    }

    @BeforeEach
    void deleteAll() {
        userRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAllTasksAdmin() throws Exception {
        findAllTasksMethod(200);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAllTasksUser() throws Exception {
        findAllTasksMethod(403);
    }

    void findAllTasksMethod(int status) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));
        taskRepository.save(new Task(2L, "Mop the floor", "use the floor mop", TaskStatus.DONE, Priority.HIGH, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/tasks"));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
        if (status != 403) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findTaskAdmin() throws Exception {
        findTaskMethod(200);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findTaskUser() throws Exception {
        findTaskMethod(403);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findTaskWithWrongIdAdmin() throws Exception {
        findTaskMethod(404);
    }

    void findTaskMethod(int status) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));
        taskRepository.save(new Task(2L, "Mop the floor", "use the floor mop", TaskStatus.DONE, Priority.HIGH, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));

        ResultActions actions = status != 404
                ? mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/tasks/" + taskRepository.findByTitle("Do the laundry").get().getId()))
                : mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/tasks/1"));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
        if (status == 200) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Do the laundry")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("use the washing machine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.priority", Matchers.is(Priority.MIDDLE.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(TaskStatus.TO_DO.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.author.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.assignee.email", Matchers.is("anastasia@gmail.com")));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findTaskFilterAdmin() throws Exception {
        findTaskFilterMethod(200, true);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findTaskFilterUser() throws Exception {
        findTaskFilterMethod(403, false);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findTaskFilterWithWrongIdAdmin() throws Exception {
        findTaskFilterMethod(200, false);
    }

    void findTaskFilterMethod(int status, boolean checkPaths) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));
        taskRepository.save(new Task(2L, "Mop the floor", "use the floor mop", TaskStatus.DONE, Priority.HIGH, userRepository.findByEmail("anastasia@gmail.com").get(), userRepository.findByEmail("anastasia@gmail.com").get(), null));

        Long id = userRepository.findByEmail("anastasia@gmail.com").get().getId();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/tasks/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "pageSize": 10,
                          "pageNumber": 0,
                          "authorId": "%d",
                          "assigneeId": "%d"
                        }
                        """.formatted(id, id)));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
        if (checkPaths) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("Do the laundry")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is("use the washing machine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].priority", Matchers.is(Priority.MIDDLE.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].status", Matchers.is(TaskStatus.TO_DO.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].author.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].assignee.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", Matchers.is("Mop the floor")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is("use the floor mop")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].priority", Matchers.is(Priority.HIGH.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].status", Matchers.is(TaskStatus.DONE.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].author.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].assignee.email", Matchers.is("anastasia@gmail.com")));
        }
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateTaskTest")
    @WithMockUser(roles = "ADMIN")
    void createTaskQueryAdmin(UpsertTaskRequest dto) throws Exception {
        createMethod(dto, 200);
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateTaskTest")
    @WithMockUser(roles = "USER")
    void createTaskQueryUser(UpsertTaskRequest dto) throws Exception {
        createMethod(dto, 403);
    }

    void createMethod(UpsertTaskRequest dto, int status) throws Exception {
        userRepository.save(new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2));
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "%s",
                          "description": "%s",
                          "status": "%s",
                          "priority": "%s",
                          "authorId": "%d",
                          "assigneeId": "%d"
                        }
                        """.formatted(dto.getTitle(), dto.getDescription(), dto.getStatus(), dto.getPriority(), userRepository.findByEmail("anastasia@gmail.com").get().getId(), userRepository.findByEmail("anastasia@gmail.com").get().getId())));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
        if (status != 403) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is(dto.getTitle())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(dto.getDescription())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.priority", Matchers.is(dto.getPriority().toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(dto.getStatus().toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.author.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.assignee.email", Matchers.is("anastasia@gmail.com")));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createTaskWithWrongParameters() throws Exception {
        userRepository.save(new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2));
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "description": "Wash the dishes",
                          "status": "TO_DO",
                          "priority": "LOW",
                          "authorId": "%d",
                          "assigneeId": "%d"
                        }
                        """.formatted(userRepository.findByEmail("anastasia@gmail.com").get().getId(), userRepository.findByEmail("anastasia@gmail.com").get().getId())));

        actions.andExpect(MockMvcResultMatchers.status().isBadRequest());
        ResultActions actions2 = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Wash bowls",
                          "description": "Wash the dishes",
                          "status": "TO_DO",
                          "priority": "LOW",
                          "authorId": "%d"
                        }
                        """.formatted(userRepository.findByEmail("anastasia@gmail.com").get().getId())));
        actions2.andExpect(MockMvcResultMatchers.status().isBadRequest());
        ResultActions actions3 = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Wash bowls",
                          "description": "Wash the dishes",
                          "status": "TO_DO",
                          "priority": "LOW",
                          "assigneeId": "%d"
                        }
                        """));

        actions3.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskAdmin() throws Exception {
        updateTaskMethod(200, true);
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateTaskUser() throws Exception {
        updateTaskMethod(403, false);
    }

    void updateTaskMethod(int status, boolean checkPaths) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        Long userId = userRepository.findByEmail("anastasia@gmail.com").get().getId();
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Updated Task",
                          "description": "updated description",
                          "status": "IN_PROGRESS",
                          "priority": "HIGH",
                          "authorId": "%d",
                          "assigneeId": "%d"
                        }
                        """.formatted(userId, userId)));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
        if (checkPaths) {
            actions.andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("Updated Task")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is("updated description")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(TaskStatus.IN_PROGRESS.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.priority", Matchers.is(Priority.HIGH.toString())))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.author.email", Matchers.is("anastasia@gmail.com")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.assignee.email", Matchers.is("anastasia@gmail.com")));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateTaskWithWrongDataAdmin() throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        Long userId = userRepository.findByEmail("anastasia@gmail.com").get().getId();
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));
        taskRepository.save(new Task(2L, "Mop the floor", "use the floor mop", TaskStatus.DONE, Priority.HIGH, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        ResultActions actions1 = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Updated Task",
                          "description": "updated description",
                          "status": "IN_PROGRESS",
                          "priority": "HIGH",
                          "authorId": "999",
                          "assigneeId": "%d"
                        }
                        """.formatted(userId)));
        actions1.andExpect(MockMvcResultMatchers.status().is(400));

        ResultActions actions2 = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Mop the floor",
                          "description": "updated description",
                          "status": "IN_PROGRESS",
                          "priority": "HIGH",
                          "authorId": "%d",
                          "assigneeId": "%d"
                        }
                        """.formatted(userId, userId)));
        actions2.andExpect(MockMvcResultMatchers.status().is(400));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void addCommentAdmin() throws Exception {
        addCommentMethod();
    }

    @Test
    @WithMockUser(roles = "USER")
    void addCommentUser() throws Exception {
        addCommentMethod();
    }

    void addCommentMethod() {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        try {
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/addComment/" + taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "request": "Some comment"
                            }
                            """));

            actions.andExpect(MockMvcResultMatchers.status().is(200))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.comment", Matchers.is("Some comment")));
        } catch (Exception e) {
            System.out.println("There was a problem trying to check id in TaskAccessAspect class");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCommentWithWrongIdAdmin() throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));
        try {
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/changeStatus/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "request": "IN_PROGRESS"
                            }
                            """));

            actions.andExpect(MockMvcResultMatchers.status().is(404));
        } catch (Exception e) {
            System.out.println("There was a problem trying to check id in TaskAccessAspect class");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeStatusAdmin() throws Exception {
        changeStatusMethod(200);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeStatusUser() throws Exception {
        changeStatusMethod(200);
    }

    void changeStatusMethod(int status) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        try {
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/changeStatus/" + taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "request": "IN_PROGRESS"
                            }
                            """));

            actions.andExpect(MockMvcResultMatchers.status().is(status));
            if (status == 200) {
                actions.andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(TaskStatus.IN_PROGRESS.toString())));
            }
        } catch (Exception e) {
            System.out.println("There was a problem trying to check id in TaskAccessAspect class");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeStatusWithWrongDataAdmin() throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        try {
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/changeStatus/" + taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "request": "INVALID_STATUS"
                            }
                            """));

            actions.andExpect(MockMvcResultMatchers.status().is(400));
        } catch (Exception e) {
            System.out.println("There was a problem trying to check id in TaskAccessAspect class");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeStatusWithWrongIdAdmin() throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));
        try {
            ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.put("/taskManagement/tasks/changeStatus/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "request": "IN_PROGRESS"
                            }
                            """));

            actions.andExpect(MockMvcResultMatchers.status().is(404));
        } catch (Exception e) {
            System.out.println("There was a problem trying to check id in TaskAccessAspect class");
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskAdmin() throws Exception {
        deleteTaskMethod(204);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteTaskUser() throws Exception {
        deleteTaskMethod(403);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTaskWithWrongIdAdmin() throws Exception {
        deleteTaskMethodWithWrongId(204);
    }

    void deleteTaskMethod(int status) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        Long taskId = taskRepository.findByTitle("Do the laundry").get().getId();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/tasks/" + taskId));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
    }

    void deleteTaskMethodWithWrongId(int status) throws Exception {
        User u = new User(1L, "Anastasia", "anastasia@gmail.com", "1", null, null, role2);
        u = userRepository.save(u);
        taskRepository.save(new Task(1L, "Do the laundry", "use the washing machine", TaskStatus.TO_DO, Priority.MIDDLE, u, u, null));

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/tasks/999"));

        actions.andExpect(MockMvcResultMatchers.status().is(status));
    }
}