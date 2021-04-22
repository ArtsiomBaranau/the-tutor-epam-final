package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.repositories.QuizRepository;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public List<Quiz> findAll() {
        List<Quiz> quizzes = new ArrayList<>();
        quizRepository.findAll().forEach(quizzes::add);
        return quizzes;
    }

    @Override
    public Quiz findById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    @Override
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    public void delete(Quiz quiz) {
        quizRepository.delete(quiz);
    }

    @Override
    public void deleteById(Long id) {
        quizRepository.deleteById(id);
    }
}
