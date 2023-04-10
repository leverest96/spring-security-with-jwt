package com.example.test.security.controller;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Long, Users> {
    Optional<Users> findById(int id);
}
