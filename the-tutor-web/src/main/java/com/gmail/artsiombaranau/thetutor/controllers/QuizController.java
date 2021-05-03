package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.*;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import com.gmail.artsiombaranau.thetutor.utils.QuizUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final String REDIRECT_MENU = "redirect:/menu";

    private final UserService userService;
    private final QuizService quizService;
    private final SpecialtyService specialtyService;

    private final QuizUtils quizUtils;

    @GetMapping("/create")
    public String createQuizForm(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());

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

            quiz.setSpecialty(null);

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
        return REDIRECT_MENU;
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
        return REDIRECT_MENU;
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

            return REDIRECT_MENU;
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
                return REDIRECT_MENU;
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

        Map<Question, Map<Answer, Boolean>> questionsMap = new LinkedHashMap<>();

        for (int itemQuestion = 0; itemQuestion < questionsQuantity; itemQuestion++) {
            Question originalQuestion = originalQuestions.get(itemQuestion);
            Question passedQuestion = passedQuestions.get(itemQuestion);

            Map<Answer, Boolean> answersMap = new LinkedHashMap<>();

            List<Answer> originalAnswers = originalQuestion.getAnswers();
            List<Answer> passedAnswers = passedQuestion.getAnswers();

            if (passedAnswers.equals(originalAnswers)) {
                rightQuestionsQuantity++;
            }

            for (int itemAnswer = 0; itemAnswer < 4; itemAnswer++) {
                Answer originalAnswer = originalAnswers.get(itemAnswer);
                Answer passedAnswer = passedAnswers.get(itemAnswer);

                boolean isRightOriginalAnswer = originalAnswer.getIsRight();
                boolean isRightPassedAnswer = passedAnswer.getIsRight();

                if (isRightOriginalAnswer && isRightPassedAnswer) {
                    answersMap.put(originalAnswer, Boolean.TRUE);
                } else if (isRightOriginalAnswer && !isRightPassedAnswer) {
                    answersMap.put(originalAnswer, Boolean.TRUE);
                } else if (!isRightOriginalAnswer && !isRightPassedAnswer) {
                    answersMap.put(originalAnswer, null);
                } else if (!isRightOriginalAnswer && isRightPassedAnswer) {
                    answersMap.put(originalAnswer, Boolean.FALSE);
                }
            }
            questionsMap.put(originalQuestion, answersMap);
        }

        BigDecimal percentage = new BigDecimal(rightQuestionsQuantity);

        percentage = percentage.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(questionsQuantity), RoundingMode.HALF_UP);

        model.addAttribute("percentage", percentage);
        model.addAttribute("questionsMap", questionsMap);

        return "quiz/progress";
    }

    @GetMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, Model model) {
        if (id != null) {
            quizService.deleteById(id);

            return REDIRECT_MENU;
        }
        //add error to model and return error page
        return REDIRECT_MENU;
    }
}
