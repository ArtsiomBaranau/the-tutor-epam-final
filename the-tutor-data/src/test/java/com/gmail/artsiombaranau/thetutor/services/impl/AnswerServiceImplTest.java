package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.repositories.AnswerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    AnswerRepository answerRepository;

    @InjectMocks
    AnswerServiceImpl answerService;

    Answer answer;

    @BeforeEach
    void setUp() {
        answer = Answer.builder().id(1L).description("Some answer...").isRight(Boolean.FALSE).build();
    }

    @Test
    void findAll() {
//      given
        List<Answer> answers = List.of(answer);
        given(answerRepository.findAll()).willReturn(answers);
//      when
        Iterable<Answer> receivedAnswers = answerService.findAll();
//      then
        then(answerRepository).should(times(1)).findAll();
        assertIterableEquals(answers,receivedAnswers);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(answerRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        Answer returnedAnswer = answerService.findById(1L);
//        then
        then(answerRepository).should(times(1)).findById(anyLong());
        assertNull(returnedAnswer);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(answerRepository.findById(anyLong())).willReturn(Optional.of(answer));
//        when
        Answer returnedAnswer = answerService.findById(1L);
//        then
        then(answerRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedAnswer);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    void save() {
//        given
        given(answerRepository.save(any(Answer.class))).willReturn(answer);
//        when
        Answer savedAnswer = answerService.save(answer);
//        then
        then(answerRepository).should(times(1)).save(any(Answer.class));
        assertNotNull(savedAnswer);
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    void delete() {
//        when
        answerService.delete(answer);
//        then
        then(answerRepository).should(times(1)).delete(any(Answer.class));
        verifyNoMoreInteractions(answerRepository);
    }

    @Test
    void deleteById() {
//        when
        answerService.deleteById(1L);
//        then
        then(answerRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(answerRepository);
    }
}