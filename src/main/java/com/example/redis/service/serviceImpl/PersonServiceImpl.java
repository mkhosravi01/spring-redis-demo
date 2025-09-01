package com.example.redis.service.serviceImpl;

import com.example.redis.dto.PersonResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.redis.dto.PersonRequestDTO;
import com.example.redis.entity.Person;
import com.example.redis.repository.PersonRepository;
import com.example.redis.service.PersonService;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    public PersonResponseDTO createPerson(PersonRequestDTO personDTO) {
        Person personResponse = personRepository.save(toEntity(personDTO));
        return toDTO(personResponse);
    }

    @Cacheable(value = "persons", key = "#id")
    public PersonResponseDTO getPerson(Long id) {
        System.out.println("Fetching from DB...");
        Person person = personRepository.findById(id).orElse(null);
        if (person == null) {
            return null;
        }
        return toDTO(person);
    }

    private Person toEntity(PersonRequestDTO personDTO) {
        return Person.builder()
                .firstName(personDTO.getFirstName())
                .lastName(personDTO.getLastName())
                .email(personDTO.getEmail())
                .phone(personDTO.getPhone())
                .address(personDTO.getAddress())
                .interests(personDTO.getInterests()).build();
    }

    private PersonResponseDTO toDTO(Person person) {
        return PersonResponseDTO.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .email(person.getEmail())
                .phone(person.getPhone())
                .address(person.getAddress())
                .interests(person.getInterests()).build();
    }
}
