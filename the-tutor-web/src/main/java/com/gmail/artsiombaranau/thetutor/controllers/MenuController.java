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
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private static final String MENU = "menu";

    private final QuizService quizService;
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String getMenu(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {

        return getMenuPage(principal, 1, model);
    }

    @GetMapping("/{pageNumber}")
    public String getMenuPage(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable int pageNumber, Model model) {
        int pageSize = 3;

        if (principal.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.name()))) {
            Page<User> page = userService.findPaginated(pageNumber, pageSize);

            List<User> users = page.getContent();

            Role roleAdmin = roleService.findByName(Roles.ADMIN);

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalQuizzes", page.getTotalElements());
            model.addAttribute("users", users);
            model.addAttribute("roleAdmin", roleAdmin);

        } else {
            Page<Quiz> page = quizService.findPaginated(pageNumber, pageSize);

            List<Quiz> quizzes = page.getContent();

            model.addAttribute("currentPage", pageNumber);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalQuizzes", page.getTotalElements());
            model.addAttribute("quizzes", quizzes);
        }

        return MENU;
    }
}
