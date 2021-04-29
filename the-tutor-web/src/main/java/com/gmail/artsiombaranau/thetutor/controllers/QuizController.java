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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            model.addAttribute("specialtiesList", specialties);

            return CREATE_OR_UPDATE;
        } else {
            model.addAttribute("error", "Please, authorize yourself one more time!");

            return "index";
        }
    }

    @PostMapping("/create")
    public String saveQuiz(@ModelAttribute @Valid Quiz quiz, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Specialty> specialties = specialtyService.findAll();

            model.addAttribute("specialtiesList", specialties);
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
        Quiz quiz = quizService.findById(id);

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
            List<Specialty> specialties = specialtyService.findAll();

            model.addAttribute("specialtiesList", specialties);
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
        int questionsQuantity = passedQuiz.getQuestions().size();
        int rightQuestionsQuantity = 0;

        Quiz originalQuiz = quizService.findById(passedQuiz.getId());

        List<Question> passedQuestions = passedQuiz.getQuestions();
        List<Question> originalQuestions = originalQuiz.getQuestions();

        Map<Question, Boolean> questionsMap = new LinkedHashMap<>();

        for (int i = 0; i < questionsQuantity; i++) {
            Question originalQuestion = originalQuestions.get(i);
            Question passedQuestion = passedQuestions.get(i);

            if (passedQuestion.getAnswers().equals(originalQuestion.getAnswers())) {
                questionsMap.put(originalQuestion, Boolean.TRUE);

                rightQuestionsQuantity++;
            } else {
                questionsMap.put(originalQuestion, Boolean.FALSE);
            }
        }

        BigDecimal percentage = new BigDecimal(rightQuestionsQuantity);

        percentage = percentage.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(questionsQuantity), RoundingMode.HALF_UP);

        model.addAttribute("percentage", percentage);
        model.addAttribute("questionsMap", questionsMap);

        return "quiz/progress";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, Model model) {
        if (id != null) {
            quizService.deleteById(id);

            return "menu";
        }
        //add error to model and return error page
        return "menu";
    }
}
