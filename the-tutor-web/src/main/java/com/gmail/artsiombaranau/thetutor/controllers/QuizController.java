package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.*;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class QuizController {

    private static final String CREATE_OR_UPDATE = "quiz/create_or_update";

    private final UserService userService;
    private final QuizService quizService;
    private final SpecialtyService specialtyService;

    @GetMapping("/create")
    public String createQuizForm(Principal principal, Model model) {
//        User user = userService.findByUsername(principal.getName());
        User user = userService.findByUsername("tutor");
//        if (user != null) {
        Quiz quiz = createEmptyQuiz(user);
        model.addAttribute("quiz", quiz);

        List<Specialty> specialties = specialtyService.findAll();
        model.addAttribute("specialties", specialties);

//            return CREATE_OR_UPDATE;
//        }

        return CREATE_OR_UPDATE;
    }

    @PostMapping("/create")
    public String saveQuiz(@ModelAttribute @Valid Quiz quiz, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("quiz", quiz);

            return CREATE_OR_UPDATE;
        } else {
            quiz.getQuestions()
                    .forEach(question -> {
                        question.setQuiz(quiz);
                        question.getAnswers()
                                .forEach(answer -> answer.setQuestion(question));
                    });

            quizService.save(quiz);
        }
        return "index";
    }

    @GetMapping("/{id}/update")
    public String updateQuizForm(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findById(id); //do with is exists in repo

        List<Specialty> specialties = specialtyService.findAll();

        if (quiz != null && specialties != null) {
            model.addAttribute("quiz", quiz);
            model.addAttribute("specialtiesList", specialties);

            return CREATE_OR_UPDATE;
        }
        //add error to model and return error page
        return null;
    }

    @PostMapping("/update")
    public String saveUpdatedQuiz(@ModelAttribute @Valid Quiz quiz, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("quiz", quiz);

            return CREATE_OR_UPDATE;
        } else {
            Quiz updatedAndSavedQuiz = quizService.save(quiz);
            model.addAttribute("quiz", updatedAndSavedQuiz);

            return "quiz/show";
        }
        //add error to model and return error page
    }

    @GetMapping("/{id}")
    public String getQuiz(@PathVariable Long id, Model model) {
        if (id != null) {
            Quiz quiz = quizService.findById(id);
            if (quiz != null) {
                model.addAttribute(quiz);

                return "quiz/show";
            }
        }
        //add error to model and return error page
        return null;
    }

    private Quiz createEmptyQuiz(User user) {
        Quiz quiz = Quiz.builder().user(user).build();

        Question questionOne = Question.builder().quiz(quiz).build();
        List<Answer> answersQuestionOne = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            answersQuestionOne.add(Answer.builder().isRight(Boolean.FALSE).question(questionOne).build());
        }
        questionOne.setAnswers(answersQuestionOne);

        Question questionTwo = Question.builder().quiz(quiz).build();
        List<Answer> answersQuestionTwo = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            answersQuestionTwo.add(Answer.builder().isRight(Boolean.FALSE).question(questionTwo).build());
        }
        questionTwo.setAnswers(answersQuestionTwo);

//        Question questionThree = Question.builder().build();
//        List<Answer> answersQuestionThree = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionThree.add(Answer.builder().isRight(Boolean.FALSE).question(questionThree).build());
//        }
//        questionThree.setAnswers(answersQuestionThree);
//
//        Question questionFour = Question.builder().build();
//        List<Answer> answersQuestionFour = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionFour.add(Answer.builder().isRight(Boolean.FALSE).question(questionFour).build());
//        }
//        questionFour.setAnswers(answersQuestionFour);
//
//        Question questionFive = Question.builder().build();
//        List<Answer> answersQuestionFive = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionFive.add(Answer.builder().isRight(Boolean.FALSE).question(questionFive).build());
//        }
//        questionFive.setAnswers(answersQuestionFive);
//
//        Question questionSix = Question.builder().build();
//        List<Answer> answersQuestionSix = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionSix.add(Answer.builder().isRight(Boolean.FALSE).question(questionSix).build());
//        }
//        questionSix.setAnswers(answersQuestionSix);
//
//        Question questionSeven = Question.builder().build();
//        List<Answer> answersQuestionSeven = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionSeven.add(Answer.builder().isRight(Boolean.FALSE).question(questionSeven).build());
//        }
//        questionSeven.setAnswers(answersQuestionSeven);

//        List<Question> questions = new ArrayList<>(List.of(questionOne, questionTwo, questionThree, questionFour, questionFive, questionSix, questionSeven));
        List<Question> questions = new ArrayList<>(List.of(questionOne, questionTwo));

        quiz.setQuestions(questions);

        return quiz;
    }
}
