package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.*;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import com.gmail.artsiombaranau.thetutor.services.QuizCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    private static final String CHOOSE_QUESTIONS_QUANTITY = "quiz/choose_questions_quantity";
    private static final String CREATE_OR_UPDATE = "quiz/create_or_update";
    private static final String PASS_QUIZ = "quiz/pass";
    private static final String REDIRECT_MENU = "redirect:/menu";
    private static final String QUIZ_PROGRESS = "quiz/progress";
    private static final String ERROR = "error";

    @Mock
    UserService userService;
    @Mock
    QuizService quizService;
    @Mock
    SpecialtyService specialtyService;
    @Mock
    QuizCreatorService quizCreatorService;

    @Mock
    Quiz quiz;
    @Mock
    Quiz emptyQuiz;
    @Mock
    Question question;
    @Mock
    Answer answer;
    @Mock
    Model model;
    @Mock
    BindingResult bindingResult;
    @Mock
    UserDetailsImpl principal;

    @InjectMocks
    QuizController quizController;

    User user;
    List<Specialty> specialties;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("username").email("user@mail.ru").password("password").firstName("John").lastName("Doe").build();
        specialties = List.of(Specialty.builder().build());
    }

    @Test
    void chooseQuestionsQuantityPage() {
        String viewName = quizController.chooseQuestionsQuantityPage();

        assertEquals(CHOOSE_QUESTIONS_QUANTITY, viewName);
    }

    @Test
    void createQuizFormQuantityIsLessThanOne() {
//        given
        given(principal.getUsername()).willReturn("username");
        given(userService.findByUsername(anyString())).willReturn(user);
//        when
        String viewName = quizController.createQuizForm(principal, 0, model);
//        then
        then(principal).should(times(1)).getUsername();
        then(userService).should(times(1)).findByUsername(anyString());
        then(model).should(times(1)).addAttribute(anyString(),anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void createQuizFormQuantityIsMoreThanOne() {
//        given
        given(principal.getUsername()).willReturn("username");
        given(userService.findByUsername(anyString())).willReturn(user);
        given(quizCreatorService.createEmptyQuiz(any(User.class), anyInt())).willReturn(emptyQuiz);
        given(specialtyService.findAll()).willReturn(specialties);
//        when
        String viewName = quizController.createQuizForm(principal, 3, model);
//        then
        then(principal).should(times(1)).getUsername();
        then(userService).should(times(1)).findByUsername(anyString());
        then(quizCreatorService).should(times(1)).createEmptyQuiz(any(User.class), anyInt());
        then(specialtyService).should(times(1)).findAll();
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveQuizHasErrors() {
//      given
        given(bindingResult.hasErrors()).willReturn(true);
        given(specialtyService.findAll()).willReturn(specialties);
//      when
        String viewName = quizController.saveQuiz(quiz, bindingResult, model);
//      then
        then(model).should(times(2)).addAttribute(anyString(), any());
        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveQuizHasNoErrorsAndWasSaved() {
//      given
        given(bindingResult.hasErrors()).willReturn(false);
        given(quizService.save(any(Quiz.class))).willReturn(quiz);

//      when
        String viewName = quizController.saveQuiz(quiz, bindingResult, model);
//      then
        then(bindingResult).should(times(1)).hasErrors();
        then(quizService).should(times(1)).save(any(Quiz.class));
        assertEquals(REDIRECT_MENU, viewName);
    }

    @Test
    void saveQuizHasNoErrorsAndWasNotSaved() {
//      given
        given(bindingResult.hasErrors()).willReturn(false);
        given(quizService.save(any(Quiz.class))).willReturn(null);
//      when
        String viewName = quizController.saveQuiz(quiz, bindingResult, model);
//      then
        then(bindingResult).should(times(1)).hasErrors();
        then(quizService).should(times(1)).save(any(Quiz.class));
        then(model).should(times(1)).addAttribute(anyString(), any(Quiz.class));
        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void updateQuizFormQuizIsNull() {
//        given
        given(quizService.findById(anyLong())).willReturn(null);
//        when
        String viewName = quizController.updateQuizForm(1L, model);
//        then
        then(quizService).should(times(1)).findById(anyLong());
        then(model).should(times(1)).addAttribute(anyString(), anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void updateQuizFormQuizIsNotNull() {
//        given
        given(quizService.findById(anyLong())).willReturn(quiz);
        given(specialtyService.findAll()).willReturn(specialties);
//        when
        String viewName = quizController.updateQuizForm(1L, model);
//        then
        then(quizService).should(times(1)).findById(anyLong());
        then(specialtyService).should(times(1)).findAll();
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedQuizHasErrors() {
//      given
        given(bindingResult.hasErrors()).willReturn(true);
        given(specialtyService.findAll()).willReturn(specialties);
//      when
        String viewName = quizController.saveUpdatedQuiz(quiz, bindingResult, model);
//      then
        then(bindingResult).should(times(1)).hasErrors();
        then(specialtyService).should(times(1)).findAll();
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedQuizHasNoErrorsSaved() {
//        given
        given(bindingResult.hasErrors()).willReturn(false);
        given(quizService.save(any(Quiz.class))).willReturn(quiz);
//        when
        String viewName = quizController.saveUpdatedQuiz(quiz, bindingResult, model);
//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(quizService).should(times(1)).save(any(Quiz.class));

        assertEquals(REDIRECT_MENU, viewName);
    }

    @Test
    void saveUpdatedQuizHasNoErrorsNotSaved() {
//        given
        given(bindingResult.hasErrors()).willReturn(false);
        given(quizService.save(any(Quiz.class))).willReturn(null);
//        when
        String viewName = quizController.saveUpdatedQuiz(quiz, bindingResult, model);

//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(model).should(times(1)).addAttribute(anyString(), any(Quiz.class));
        then(model).should(times(1)).addAttribute(anyString(), any(Quiz.class));

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void getQuizNotFound() {
//        given
        given(quizService.findById(anyLong())).willReturn(null);
//        when
        String viewName = quizController.getQuiz(1L, model);
//        then
        then(quizService).should(times(1)).findById(anyLong());
        then(model).should(times(1)).addAttribute(anyString(), anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void getQuizFound() {
//        given
        given(quizService.findById(anyLong())).willReturn(quiz);
//        when
        String viewName = quizController.getQuiz(1L, model);
//        then
        then(quizService).should(times(1)).findById(anyLong());
        then(model).should(times(1)).addAttribute(anyString(), any(Quiz.class));

        assertEquals(PASS_QUIZ, viewName);
    }

    @Test
    void passQuiz() {
//        given
        given(quiz.getId()).willReturn(1L);
        given(quizService.findById(anyLong())).willReturn(quiz);
        given(quiz.getQuestions()).willReturn(List.of(question));
        given(question.getAnswers()).willReturn(List.of(answer, answer, answer, answer));
        given(answer.getIsRight()).willReturn(false);
//        when
        String viewName = quizController.passQuiz(quiz, model);
//        then
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(QUIZ_PROGRESS, viewName);
    }

    @Test
    void deleteQuiz() {
//        when
        String viewName = quizController.deleteQuiz(1L);
//        then
        then(quizService).should(times(1)).deleteById(anyLong());

        assertEquals(REDIRECT_MENU, viewName);
    }
}