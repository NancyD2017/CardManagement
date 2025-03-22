package com.example.TaskManagement.controller;

import com.example.TaskManagement.model.entity.Role;
import com.example.TaskManagement.model.entity.User;
import com.example.TaskManagement.model.request.UpsertUserRequest;
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(dto.getUsername())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(dto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles", Matchers.contains(dto.getRoles().iterator().next().name())));
    }

    @Test
    void createUserWithWrongParameters() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post("/taskManagement/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Dmitry",
                          "email": "dmitry@yandex.ru",
                          "roles": ["ROLE_USER"]
                        }
                        """));

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
        userRepository.save(new User(1L, "Anna", "anna@gmail.com", "1", null, null, roles2));
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users/" + userRepository.findByEmail("anna@gmail.com").get().getId()));

        actions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is("Anna")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("anna@gmail.com")));

    }

    @Test
    void findByIdMethodWithoutExistUser() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users/1"));

        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void findAllUsers() throws Exception {
        userRepository.save(new User(1L, "Nicolay", "nicolay@gmail.com", "1", null, null, roles));
        userRepository.save(new User(1L, "Svetlana", "svetlana@gmail.com", "1", null, null, roles2));

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get("/taskManagement/users"));

        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.notNullValue()));
    }

    @Test
    void deleteUserById() throws Exception {
        userRepository.save(new User(1L, "Ilya", "ilya@gmail.com", "1", null, null, roles));

        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/users/" + userRepository.findByEmail("ilya@gmail.com").get().getId()));

        actions.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteUserByIdWithoutExistUser() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.delete("/taskManagement/users/1"));

        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}