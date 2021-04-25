package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.*;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import com.gmail.artsiombaranau.thetutor.utils.QuizUtils;
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
    private static final String PASS_QUIZ = "quiz/pass";

    private final UserService userService;
    private final QuizService quizService;
    private final SpecialtyService specialtyService;

    private final QuizUtils quizUtils;

    @GetMapping("/create")
    public String createQuizForm(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());

        if (user != null) {
            Quiz quiz = quizUtils.createEmptyQuiz(user);

            model.addAttribute("quiz", quiz);

            List<Specialty> specialties = specialtyService.findAll();

            model.addAttribute("specialties", specialties);

            return CREATE_OR_UPDATE;
        } else {
            model.addAttribute("error", "Please, authorize yourself one more time!");

            return "index";
        }
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

            return PASS_QUIZ;
        }
        //add error to model and return error page
    }

    @GetMapping("/{id}")
    public String getQuiz(@PathVariable Long id, Model model) {
        if (id != null) {
            Quiz quiz = quizService.findById(id);

            if (quiz != null) {
                quiz.getQuestions().forEach(question -> {
                    question.getAnswers().forEach(answer -> {
                        answer.setIsRight(Boolean.FALSE);
                    });
                });
                model.addAttribute(quiz);

                return PASS_QUIZ;
            } else {
                model.addAttribute("error", "Quiz with id: " + id + " doesn't exist!");
                return "quiz/list"; //create page with list of quizzes
            }
        }
        //add error to model and return error page
        return null;
    }

    @PostMapping("/pass")
    public String passQuiz(@ModelAttribute Quiz passedQuiz, Model model) {

        Quiz originalQuiz = quizService.findById(passedQuiz.getId());

        List<Answer> rightAnswers = new ArrayList<>();
        originalQuiz.getQuestions().forEach(question -> rightAnswers.addAll(question.getAnswers()));

        List<Answer> passedAnswers = new ArrayList<>();
        passedQuiz.getQuestions().forEach(question -> passedAnswers.addAll(question.getAnswers()));

        //check results and return progressbar

        return PASS_QUIZ;
    }
}
