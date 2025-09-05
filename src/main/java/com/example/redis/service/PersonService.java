package com.example.redis.service;

import com.example.redis.dto.PersonResponseDTO;
import com.example.redis.dto.PersonRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface PersonService {
    PersonResponseDTO createPerson(PersonRequestDTO person);


    PersonResponseDTO getPerson(Long id) throws JsonProcessingException;
}
