package com.gmail.artsiombaranau.thetutor.repositories;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
