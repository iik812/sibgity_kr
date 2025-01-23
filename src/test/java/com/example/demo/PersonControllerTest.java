package com.example.demo;

import com.example.demo.controller.PersonChangeController;
import com.example.demo.dto.Person;
import com.example.demo.repository.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PersonControllerTest {

    private Repository repository;
    private PersonChangeController controller;

    @BeforeEach
    void setUp() {
        repository = mock(Repository.class);
        controller = new PersonChangeController(repository);
    }

    @Test
    void getPerson_ShouldReturnPerson_WhenExists() {
        long id = 1L;
        Person person = new Person(id, "John Doe", 30);
        when(repository.findById(id)).thenReturn(Optional.of(person));

        ResponseEntity<Optional<Person>> response = controller.getPerson(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Optional.of(person), response.getBody());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void getPerson_ShouldReturnEmpty_WhenNotFound() {
        long id = 2L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<Optional<Person>> response = controller.getPerson(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(Optional.empty(), response.getBody());
        verify(repository, times(1)).findById(id);
    }

    @Test
    void addPerson_ShouldReturnBadRequest_WhenNameIsEmpty() {
        Person invalidPerson = new Person(0, "", 25);

        ResponseEntity<Object> response = controller.addPerson(invalidPerson);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Некорректные данные", responseBody.get("error"));
        assertEquals("Имя не может быть пустым", responseBody.get("message"));
    }

    @Test
    void addPerson_ShouldAddPerson_WhenValid() {
        Person validPerson = new Person(0, "Alice", 25);
        Person savedPerson = new Person(1, "Alice", 25);
        when(repository.save(validPerson)).thenReturn(savedPerson);

        ResponseEntity<Object> response = controller.addPerson(validPerson);

        assertEquals(201, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Запись успешно добавлена", responseBody.get("message"));
        assertEquals(savedPerson, responseBody.get("person"));
        verify(repository, times(1)).save(validPerson);
    }

    @Test
    void delPerson_ShouldReturnNotFound_WhenPersonDoesNotExist() {
        long id = 3L;
        when(repository.existsById(id)).thenReturn(false);

        ResponseEntity<String> response = controller.delPerson(id);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Запись с ID 3 не найдена, удаление невозможно", response.getBody());
        verify(repository, times(1)).existsById(id);
    }

    @Test
    void delPerson_ShouldDeletePerson_WhenPersonExists() {
        long id = 4L;
        when(repository.existsById(id)).thenReturn(true);

        ResponseEntity<String> response = controller.delPerson(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Запись успешно удалена", response.getBody());
        verify(repository, times(1)).existsById(id);
        verify(repository, times(1)).deleteById(id);
    }
}
