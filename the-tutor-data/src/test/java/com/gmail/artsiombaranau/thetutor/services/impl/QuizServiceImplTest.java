package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.enums.Specialties;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.Specialty;
import com.gmail.artsiombaranau.thetutor.repositories.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    QuizRepository quizRepository;

    @InjectMocks
    QuizServiceImpl quizService;

    Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = Quiz.builder().id(1L).name("Name.").description("Some description...").specialty(Specialty.builder().id(1L).description(Specialties.MATH).build()).build();
    }

    @Test
    void findAll() {
//      given
        List<Quiz> quizzes = List.of(quiz);
        given(quizRepository.findAll()).willReturn(quizzes);
//      when
        Iterable<Quiz> receivedQuizzes = quizService.findAll();
//      then
        then(quizRepository).should(times(1)).findAll();
        assertIterableEquals(quizzes,receivedQuizzes);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void findPaginated() {
//      given
        Page<Quiz> page = Mockito.mock(Page.class);
        given(quizRepository.findAll(any(Pageable.class))).willReturn(page);
//      when
        Page<Quiz> receivedPage = quizService.findPaginated(1,3);
//      then
        then(quizRepository).should(times(1)).findAll(any(Pageable.class));
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(quizRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        Quiz returnedQuiz = quizService.findById(1L);
//        then
        then(quizRepository).should(times(1)).findById(anyLong());
        assertNull(returnedQuiz);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(quizRepository.findById(anyLong())).willReturn(Optional.of(quiz));
//        when
        Quiz returnedQuiz = quizService.findById(1L);
//        then
        then(quizRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedQuiz);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void save() {
//        given
        given(quizRepository.save(any(Quiz.class))).willReturn(quiz);
//        when
        Quiz savedQuiz = quizService.save(quiz);
//        then
        then(quizRepository).should(times(1)).save(any(Quiz.class));
        assertNotNull(savedQuiz);
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void delete() {
//        when
        quizService.delete(quiz);
//        then
        then(quizRepository).should(times(1)).delete(any(Quiz.class));
        verifyNoMoreInteractions(quizRepository);
    }

    @Test
    void deleteById() {
//        when
        quizService.deleteById(1L);
//        then
        then(quizRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(quizRepository);
    }
}