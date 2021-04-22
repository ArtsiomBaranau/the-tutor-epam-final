package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.repositories.AnswerRepository;
import com.gmail.artsiombaranau.thetutor.services.AnswerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<Answer> findAll() {
        List<Answer> answers = new ArrayList<>();
        answerRepository.findAll().forEach(answers::add);
        return answers;
    }

    @Override
    public Answer findById(Long id) {
        return answerRepository.findById(id).orElse(null);
    }

    @Override
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public void deleteById(Long id) {
        answerRepository.deleteById(id);
    }
}
