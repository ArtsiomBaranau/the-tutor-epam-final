package com.gmail.artsiombaranau.thetutor.repositories;

import com.gmail.artsiombaranau.thetutor.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String usermame);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
