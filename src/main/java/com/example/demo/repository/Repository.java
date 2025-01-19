package com.example.demo.repository;

import com.example.demo.dto.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Repository extends JpaRepository<Person, Long> {
    Person findAllById(long id);
}
