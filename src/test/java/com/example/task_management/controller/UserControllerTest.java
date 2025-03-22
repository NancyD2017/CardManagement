package com.example.task_management.controller;

import com.example.task_management.model.entity.Role;
import com.example.task_management.model.entity.User;
import com.example.task_management.model.request.UpsertUserRequest;
import com.example.task_management.repository.UserRepository;
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
public class UserControllerTest {
    public static Set<Role> roles = new HashSet<>(Collections.singleton(Role.ROLE_USER));
    public static Set<Role> roles2 = new HashSet<>(Collections.singleton(Role.ROLE_ADMIN));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    static Stream<Arguments> argumentsForCreateUserTest() {
        return Stream.of(
                Arguments.of(new UpsertUserRequest("Ivan", "vanya@yandex.ru", roles, "1")),
                Arguments.of(new UpsertUserRequest("Petr", "petya@yandex.ru", roles2, "2")),
                Arguments.of(new UpsertUserRequest("Vasilisa", "vassa@yandex.ru", roles, "3"))
        );
    }

    static Stream<Arguments> argumentsForCreateUserWithWrongParameters() {
        return Stream.of(
                Arguments.of("""
                        {
                          "username": "Dmitry",
                          "email": "dmitry@yandex.ru",
                          "roles": ["ROLE_USER"]
                        }
                        """, "Missing password"),
                Arguments.of("""
                        {
                          "username": "Dmitry",
                          "email": "invalid-email",
                          "roles": ["ROLE_USER"],
                          "password": "1"
                        }
                        """, "Invalid email"),
                Arguments.of("""
                        {
                          "username": "Dmitry",
                          "email": "dmitry@yandex.ru",
                          "password": "1"
                        }
                        """, "Missing roles")
        );
    }

    @BeforeEach
    void deleteAll() {
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateUserTest")
    void createUserQuery(UpsertUserRequest dto) throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "%s",
                          "email": "%s",
                          "password": "%s",
                          "roles": ["%s"]
                        }
                        """.formatted(dto.getUsername(), dto.getEmail(), dto.getPassword(), dto.getRoles().iterator().next().name())));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(dto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(dto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.contains(dto.getRoles().iterator().next().name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @ParameterizedTest
    @MethodSource("argumentsForCreateUserWithWrongParameters")
    void createUserWithWrongParameters(String requestBody, String testCase) throws Exception {
        if (testCase.equals("Duplicate email")) {
            userRepository.save(new User(null, "ExistingUser", "dmitry@yandex.ru", "1", null, null, roles));
        }

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        actions.andExpect(MockMvcResultMatchers.status().isBadRequest());
        ResultActions actions2 = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Dmitry",
                          "email": "dmitry@yandex.ru",
                          "roles": ["ROLE_USER"],
                          "password": "1"
                        }
                        """));
        actions2.andExpect(MockMvcResultMatchers.status().isOk());
        ResultActions actions3 = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Dmitry",
                          "email": "dmitry@yandex.ru",
                          "roles": ["ROLE_USER"],
                          "password": "1"
                        }
                        """));
        actions3.andExpect(MockMvcResultMatchers.status().isBadRequest());
        ResultActions actions4 = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Ekaterina",
                          "email": "Ekaterina@yandex.ru",
                          "roles": ["ROLE_USER"]
                        }
                        """));

        actions4.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void findByIdMethodTest() throws Exception {
        User user = new User(null, "Anna", "anna@gmail.com", "1", null, null, roles2);
        user = userRepository.save(user);
        Long userId = user.getId();

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users/" + userId));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userId.intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("Anna")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("anna@gmail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.contains(Role.ROLE_ADMIN.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    void findByIdMethodWithoutExistUser() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users/999"));

        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findAllUsers() throws Exception {
        userRepository.save(new User(null, "Nicolay", "nicolay@gmail.com", "1", null, null, roles));
        userRepository.save(new User(null, "Svetlana", "svetlana@gmail.com", "1", null, null, roles2));

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users"));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.users", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[0].username", Matchers.is("Nicolay")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[0].email", Matchers.is("nicolay@gmail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[0].roles", Matchers.contains(Role.ROLE_USER.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[0].password").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[1].username", Matchers.is("Svetlana")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[1].email", Matchers.is("svetlana@gmail.com")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[1].roles", Matchers.contains(Role.ROLE_ADMIN.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.users[1].password").doesNotExist());
    }

    @Test
    void deleteUserById() throws Exception {
        User user = new User(null, "Ilya", "ilya@gmail.com", "1", null, null, roles);
        user = userRepository.save(user);
        Long userId = user.getId();

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/users/" + userId));

        actions.andExpect(MockMvcResultMatchers.status().isNoContent());

        ResultActions findAfterDelete = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users/" + userId));
        findAfterDelete.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteUserByIdWithoutExistUser() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/users/999"));

        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}