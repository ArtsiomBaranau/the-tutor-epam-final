package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
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
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
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
    private final SessionRegistry sessionRegistry;

    @Qualifier(value = "userToUserDetailsConverter")
    private final Converter<User, UserDetailsImpl> userToUserDetailsConverter;

    @GetMapping("/{username}")
    public String getUser(@PathVariable String username, @AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User user = userService.findByUsername(username);

        if (user != null) {
            if (principal.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.name()))) {
                Role roleAdmin = roleService.findByName(Roles.ADMIN);

                model.addAttribute("roleAdmin", roleAdmin);
                model.addAttribute("user", user);

                return PROFILE;
            } else {
                model.addAttribute("user", user);

                return PROFILE;
            }
        } else {
            return "redirect:/menu";
        }
    }

    @GetMapping("/update")
    public String updateUserForm(@AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());

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

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal, Model model) {
        User userPrincipal = userService.findByUsername(principal.getUsername());
        User userToDelete = userService.findById(id);

        if (userPrincipal.getUsername().equals(userToDelete.getUsername())) {
            userService.deleteById(id);

            log.info("User with id: {} was deleted!", id);

            return "redirect:/logout";
        } else if (userPrincipal.getRoles().contains(roleService.findByName(Roles.ADMIN))) {
            userService.deleteById(id);

//          invalidate session for deleted user!
            List<Object> loggedUsers = sessionRegistry.getAllPrincipals();

            for (Object loggedUser : loggedUsers) {
                if (((UserDetails) loggedUser).getUsername().equals(userToDelete.getUsername())) {

                    List<SessionInformation> loggedUserSessions = sessionRegistry.getAllSessions(loggedUser, false);

                    for (SessionInformation sessionInformation : loggedUserSessions) {
                        sessionInformation.expireNow();
                    }
                }
            }


            log.info("User with id: {} was deleted by admin: {}!", id, userPrincipal.getUsername());

            return "redirect:/menu";
        } else {
            model.addAttribute("error", "You have no rights for this operation!");

            return "redirect:/menu";
        }

    }

    @GetMapping("/{id}/admin")
    public String adminUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal, Model
            model) {
        User userToAdmin = userService.findById(id);

        Role roleAdmin = roleService.findByName(Roles.ADMIN);

        if (userToAdmin.getRoles().contains(roleAdmin)) {
            userToAdmin.removeRole(roleAdmin);

            userService.save(userToAdmin);
//          invalidate session for deleted user!

            return "redirect:/menu";
        } else {
            userToAdmin.addRole(roleAdmin);

            userService.save(userToAdmin);

            log.info("User with id: {} was made to admin by principal: {}!", id, principal.getUsername());

            return "redirect:/menu";
        }
    }

}
