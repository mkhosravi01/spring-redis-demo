package com.example.redis.service.serviceImpl;

import com.example.redis.dto.PersonResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.example.redis.dto.PersonRequestDTO;
import com.example.redis.entity.Person;
import com.example.redis.repository.PersonRepository;
import com.example.redis.service.PersonService;


import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final RedisTemplate<String, PersonResponseDTO> redisTemplate;
    private static final String PERSON_KEY = "person:";

    public PersonResponseDTO createPerson(PersonRequestDTO personDTO) {
        Person personResponse = personRepository.save(toEntity(personDTO));
        return toDTO(personResponse);
    }


    @SneakyThrows
    public PersonResponseDTO getPerson(Long id) {


        String key = PERSON_KEY + id;
        PersonResponseDTO personDTO = redisTemplate.opsForValue().get(key);
        if (personDTO != null) {
            personDTO = redisTemplate.opsForValue().get(key);
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            System.out.println("time to live: " + ttl);
            System.out.println("Fetching from REDIS...");
            Thread.sleep(5000);
        }else{
            System.out.println("Fetching from DB...");
            Person person = personRepository.findById(id).orElse(null);
            personDTO = person == null ? null : toDTO(person);
            redisTemplate.opsForValue().set(key, personDTO);
            redisTemplate.expire(key, 900, TimeUnit.SECONDS);
        }

        return personDTO;
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
