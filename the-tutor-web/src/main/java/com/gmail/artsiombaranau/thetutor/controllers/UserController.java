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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/create") //add role!!!
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());

        return "user/create_or_update";
    }

    @PostMapping("/create") //add role!!!
    public String saveUser(@ModelAttribute @Valid User user, Model model) {
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = null;

        try {
            user.setRoles(Set.of(roleService.findByName(Roles.STUDENT)));
            savedUser = userService.save(user);
        } catch (UserAlreadyExistsException ex) { // maybe updating! compare id!
            model.addAttribute("error", "User with same username already exists!");
            model.addAttribute("user", user);

            return "user/create_or_update";
        }
        model.addAttribute("user", savedUser);

        return "user/show";
    }
}
