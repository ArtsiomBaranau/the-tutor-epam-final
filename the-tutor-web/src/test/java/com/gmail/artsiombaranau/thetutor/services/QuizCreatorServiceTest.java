package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QuizCreatorServiceTest {

    @Mock
    User user;

    @InjectMocks
    QuizCreatorService quizCreatorService;

    @Test
    void createEmptyQuizUserIsNull() {
//        given
        int expectedQuestionsQuantity = 7;
//        when
        Quiz createdQuiz = quizCreatorService.createEmptyQuiz(null, expectedQuestionsQuantity);
//        then
        assertNull(createdQuiz);
    }

    @ParameterizedTest
    @CsvSource({"3,3","7,7","15,15"})
    void createEmptyQuizUserIsNotNull(Integer input, Integer expected) {
//        when
        Quiz createdQuiz = quizCreatorService.createEmptyQuiz(user, input);
//        then
        assertEquals(expected, createdQuiz.getQuestions().size());
    }
}