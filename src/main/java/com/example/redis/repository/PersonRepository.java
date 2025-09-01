package com.example.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.redis.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {

}
