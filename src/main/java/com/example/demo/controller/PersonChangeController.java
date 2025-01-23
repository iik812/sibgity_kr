package com.example.demo.controller;

import com.example.demo.dto.Person;
import com.example.demo.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class PersonChangeController {
    private final Repository repository;

    public PersonChangeController(Repository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Person>> getPerson(@PathVariable("id") long id) {
        Optional<Person> person = repository.findById(id);
        return ResponseEntity.ok(person);
    }

    @PostMapping("/add")
    public ResponseEntity<Object> addPerson(@RequestBody Person person) {
        if (person == null || person.getName() == null || person.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Некорректные данные",
                            "message", "Имя не может быть пустым"
                    ));
        }

        Person savedPerson = repository.save(person);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Запись успешно добавлена",
                        "person", savedPerson
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delPerson(@PathVariable("id") long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Запись с ID " + id + " не найдена, удаление невозможно");
        }

        repository.deleteById(id);
        return ResponseEntity.ok("Запись успешно удалена");
    }
}

