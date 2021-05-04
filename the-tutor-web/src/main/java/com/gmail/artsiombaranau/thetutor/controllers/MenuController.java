package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String getMenu(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        if (principal.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.name()))) {
            List<User> users = userService.findAll();
            List<Quiz> quizzes = quizService.findAll();

            Role roleAdmin = roleService.findByName(Roles.ADMIN);

            model.addAttribute("users", users);
            model.addAttribute("roleAdmin", roleAdmin);
            model.addAttribute("quizzes", quizzes);

            return "menu";
        } else {
            List<Quiz> quizzes = quizService.findAll();

            model.addAttribute("quizzes", quizzes);

            return "menu";
        }
    }
}
