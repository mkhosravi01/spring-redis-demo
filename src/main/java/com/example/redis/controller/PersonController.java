package com.example.redis.controller;

import com.example.redis.dto.PersonResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.redis.dto.PersonRequestDTO;
import com.example.redis.entity.Person;
import com.example.redis.service.PersonService;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;


    @PostMapping("/register-person")
    public ResponseEntity<PersonResponseDTO> createPerson(@RequestBody PersonRequestDTO person) {
        PersonResponseDTO resPerson = personService.createPerson(person);
        return ResponseEntity.ok(resPerson);
    }

    @GetMapping("/getPerson/{id}")
    public ResponseEntity<PersonResponseDTO> getPersonById(@PathVariable Long id) throws JsonProcessingException {
        PersonResponseDTO person = personService.getPerson(id);
        return person != null
                ? ResponseEntity.ok(person)
                : ResponseEntity.notFound().build();
    }




}
