package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import org.springframework.data.domain.Page;

public interface QuizService extends CrudService<Quiz, Long> {
    Page<Quiz> findPaginated(int pageNumber, int pageSize);
}
