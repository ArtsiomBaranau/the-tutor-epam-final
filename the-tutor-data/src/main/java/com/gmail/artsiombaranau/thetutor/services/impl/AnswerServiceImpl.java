package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.repositories.AnswerRepository;
import com.gmail.artsiombaranau.thetutor.services.AnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;


@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Answer> findAll() {
        List<Answer> answers = new ArrayList<>();
        answerRepository.findAll().forEach(answers::add);
        return answers;
    }

    @Override
    @Transactional(readOnly = true)
    public Answer findById(Long id) {
        return answerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void deleteById(Long id) {
        answerRepository.deleteById(id);
    }
}
