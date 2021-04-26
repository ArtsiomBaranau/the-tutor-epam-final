package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final QuizService quizService;

    @GetMapping
    public String getMenu(Model model) {
        List<Quiz> quizzes = quizService.findAll();

        model.addAttribute("quizzes", quizzes);

        return "menu";
    }
}
