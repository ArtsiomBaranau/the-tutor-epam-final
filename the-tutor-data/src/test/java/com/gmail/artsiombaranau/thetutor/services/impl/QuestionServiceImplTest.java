package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.model.Question;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    QuestionServiceImpl questionService;

    Question question;

    @BeforeEach
    void setUp() {
        question = Question.builder().id(1L).description("Some question...").answers(List.of(Answer.builder().build())).quiz(Quiz.builder().build()).build();
    }

    @Test
    void findAll() {
//      given
        List<Question> questions = List.of(question);
        given(questionRepository.findAll()).willReturn(questions);
//      when
        Iterable<Question> receivedQuestions = questionService.findAll();
//      then
        then(questionRepository).should(times(1)).findAll();
        assertIterableEquals(questions,receivedQuestions);
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(questionRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        Question returnedQuestion = questionService.findById(1L);
//        then
        then(questionRepository).should(times(1)).findById(anyLong());
        assertNull(returnedQuestion);
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(questionRepository.findById(anyLong())).willReturn(Optional.of(question));
//        when
        Question returnedQuestion = questionService.findById(1L);
//        then
        then(questionRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedQuestion);
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void save() {
//        given
        given(questionRepository.save(any(Question.class))).willReturn(question);
//        when
        Question savedQuestion = questionService.save(question);
//        then
        then(questionRepository).should(times(1)).save(any(Question.class));
        assertNotNull(savedQuestion);
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void delete() {
//        when
        questionService.delete(question);
//        then
        then(questionRepository).should(times(1)).delete(any(Question.class));
        verifyNoMoreInteractions(questionRepository);
    }

    @Test
    void deleteById() {
//        when
        questionService.deleteById(1L);
//        then
        then(questionRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(questionRepository);
    }
}