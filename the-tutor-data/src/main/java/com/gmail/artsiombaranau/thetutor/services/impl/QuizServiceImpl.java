package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.repositories.QuizRepository;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> findAll() {
        return quizRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Quiz> findPaginated(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        return quizRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Quiz findById(Long id) {
        return quizRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public Quiz save(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void delete(Quiz quiz) {
        quizRepository.delete(quiz);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void deleteById(Long id) {
        quizRepository.deleteById(id);
    }
}
