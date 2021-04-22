package com.gmail.artsiombaranau.thetutor.repositories;

import com.gmail.artsiombaranau.thetutor.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question,Long> {
}
