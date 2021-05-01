package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;

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

    @Qualifier(value = "userToUserDetailsConverter")
    private final Converter<User, UserDetailsImpl> userToUserDetailsConverter;

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username, Model model) {
        User user = userService.findByUsername(username);

        if (user != null) {
            model.addAttribute("user", user);

            return PROFILE;
        } else {
            return "redirect:/menu";
        }
    }

    @GetMapping("/update")
    public String updateUserForm(Principal principal, Model model) {
        User user = userService.findByUsername(principal.getName());

        model.addAttribute("user", user);

        return CREATE_OR_UPDATE;
    }

    @PostMapping("/update")
    public String saveUpdatedUser(@ModelAttribute @Valid User user, BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);

            return CREATE_OR_UPDATE;
        } else {
            boolean existByUsername = userService.existsByUsername(user.getUsername());
            boolean isUsernameEqualsPrincipalName = user.getUsername().equals(principal.getUsername());

            if (existByUsername && !isUsernameEqualsPrincipalName) {
                String message = user.getUsername() + " already exists!";
                bindingResult.addError(new FieldError("user", "username", message));

                model.addAttribute("user", user);

                return CREATE_OR_UPDATE;
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userService.save(user);

                UserDetailsImpl userDetails = userToUserDetailsConverter.convert(savedUser);
//              update principal's data
                principal.setUsername(user.getUsername());
                principal.setPassword(user.getPassword());
                principal.setAuthorities((Collection<SimpleGrantedAuthority>) userDetails.getAuthorities());

                model.addAttribute("user", savedUser);
            }
            return PROFILE;
        }
    }

    @GetMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User userPrincipal = userService.findByUsername(principal.getUsername());
        User userToDelete = userService.findById(id);

        if (userPrincipal.getUsername().equals(userToDelete.getUsername())) {
            userService.deleteById(id);

            return "redirect:/index"; //do logout
        } else if (userPrincipal.getRoles().contains(roleService.findByName(Roles.ADMIN))) {
            userService.deleteById(id);

            return "redirect:/menu";
        } else {
            model.addAttribute("error", "You have no rights for this operation!");

            return "redirect:/menu";
        }
    }

}
