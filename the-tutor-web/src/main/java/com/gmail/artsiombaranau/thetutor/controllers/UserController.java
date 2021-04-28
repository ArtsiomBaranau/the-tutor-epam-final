package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private static final String CREATE_OR_UPDATE = "user/create_or_update";
    private static final String PROFILE = "user/profile";

    private final UserService userService;
    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);

        if (user != null) {
            model.addAttribute("user", user);

            return PROFILE;
        } else {
            return "menu";
        }
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        User user = User.builder().build();

        List<Role> roles = new ArrayList<>();
        roles.add(roleService.findByName(Roles.STUDENT));
        roles.add(roleService.findByName(Roles.TUTOR));

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roles);

        return CREATE_OR_UPDATE;
    }

    @PostMapping("/create")
    public String saveUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);

            return CREATE_OR_UPDATE;
        } else {
            boolean existByUsername = userService.existsByUsername(user.getUsername());
            boolean existByEmail = userService.existsByEmail(user.getEmail());

            if (existByUsername) {
                String message = user.getUsername() + " already exists!";
                bindingResult.addError(new FieldError("user", "username", message));

                model.addAttribute("user", user);
                return CREATE_OR_UPDATE;
            } else if (existByEmail) {
                String message = user.getEmail() + " already exists!";
                bindingResult.addError(new FieldError("user", "email", message));

                model.addAttribute("user", user);
                return CREATE_OR_UPDATE;
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userService.save(user);

                model.addAttribute("user", savedUser);

                //create and set principal

                return PROFILE;
            }
        }
    }

    @GetMapping("/update")
    public String updateUserForm(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return CREATE_OR_UPDATE;
    }

    @PostMapping("/update")
    public String saveUpdatedUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(user);

            return CREATE_OR_UPDATE;
        } else {
            boolean existByUsername = userService.existsByUsername(user.getUsername());
            boolean isUsernameEqualsPrincipalName = user.getUsername().equals(principal.getName());

            if (existByUsername && !isUsernameEqualsPrincipalName) { //add a check for existence by email
                String message = user.getUsername() + " already exists!";
                bindingResult.addError(new FieldError("user", "username", message));

                model.addAttribute(user);

                return CREATE_OR_UPDATE;
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userService.save(user);

                model.addAttribute("user", savedUser);

                return PROFILE;
            }
        }
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id, Principal principal, Model model) {
        User userPrincipal = userService.findByUsername(principal.getName());
        User userToDelete = userService.findById(id);

        if (principal.getName().equals(userToDelete.getUsername())) {
            userService.deleteById(id);

            //do logout
            return "index";
        } else if (userPrincipal.getRoles().contains(roleService.findByName(Roles.ADMIN))) {
            userService.deleteById(id);

            return "menu";
        } else {
            model.addAttribute("error", "You have no rights for this operation!");

            return "menu";
        }
    }

}
