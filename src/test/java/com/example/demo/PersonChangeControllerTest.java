package com.example.demo;


import com.example.demo.controller.PersonChangeController;
import com.example.demo.dto.Person;
import com.example.demo.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonChangeController.class)
public class PersonChangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Repository repository;

    @BeforeEach
    void setUp() {
        Mockito.when(repository.save(Mockito.any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testAddPersonSuccess() throws Exception {
        String requestBody = """
                {
                    "name": "Сергей",
                    "age": 32
                }
                """;

        mockMvc.perform(post("/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Запись успешно добавлена"))
                .andExpect(jsonPath("$.person.name").value("Сергей"))
                .andExpect(jsonPath("$.person.age").value(32));
    }

    @Test
    void testAddPersonBadRequest() throws Exception {
        String requestBody = """
                {
                    "age": 32
                }
                """;

        mockMvc.perform(post("/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Некорректные данные"))
                .andExpect(jsonPath("$.message").value("Имя не может быть пустым"));
    }
}
