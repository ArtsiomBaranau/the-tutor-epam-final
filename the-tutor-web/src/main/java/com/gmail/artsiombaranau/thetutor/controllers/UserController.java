package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.exceptions.UserAlreadyExistsException;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private static final String CREATE_OR_UPDATE = "user/create_or_update";

    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());

        return CREATE_OR_UPDATE;
    }

    @PostMapping("/create") //add role!!!
    public String saveUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);

            return CREATE_OR_UPDATE;
        } else if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "User with same username already exists!");
            model.addAttribute(user);
            //add error message to the user creation page!
            return CREATE_OR_UPDATE;
        } else {
            user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
            //set role to the user!
            User savedUser = userService.save(user);
            model.addAttribute("user", savedUser);

            return "user/show";
        }
    }
}
