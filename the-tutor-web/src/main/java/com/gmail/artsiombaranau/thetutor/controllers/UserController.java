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
    private static final String REDIRECT_MENU = "redirect:/menu";
    private static final String REDIRECT_LOGOUT = "redirect:/logout";
    private static final String ERROR = "error";

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
            model.addAttribute("error", "User with username: " + username + " not found!");

            return ERROR;
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

            if (existByUsername && !isUsernameEqualsPrincipalName) { //check this!
                String message = user.getUsername() + " already exists!";
                bindingResult.addError(new FieldError("user", "username", message));

                model.addAttribute("user", user);

                return CREATE_OR_UPDATE;
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userService.save(user);

                if (savedUser != null) {
                    UserDetailsImpl userDetails = userToUserDetailsConverter.convert(savedUser);

//              update principal's data
                    principal.setUsername(user.getUsername());
                    principal.setPassword(user.getPassword());
                    principal.setAuthorities(userDetails.getAuthorities());

                    model.addAttribute("user", savedUser);

                    return PROFILE;
                } else {
                    model.addAttribute("user", user);
                    model.addAttribute("error", "Something went wrong!");

                    return CREATE_OR_UPDATE;
                }
            }
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal, Model model) {
//        User userPrincipal = userService.findByUsername(principal.getUsername());
        User userToDelete = userService.findById(id);

        if (principal.getUsername().equals(userToDelete.getUsername())) {
            userService.deleteById(id);

            log.info("User with id: {} was deleted!", id);

            return REDIRECT_LOGOUT;
//        } else if (userPrincipal.getRoles().contains(roleService.findByName(Roles.ADMIN))) {
        } else if (principal.getAuthorities().contains(new SimpleGrantedAuthority(Roles.ADMIN.name()))) {
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

            log.info("User with id: {} was deleted by admin: {}!", id, principal.getUsername());

            return REDIRECT_MENU;
        } else {
            model.addAttribute("error", "You have no rights for this operation!");

            return ERROR;
        }
    }

    @GetMapping("/{id}/admin")
    public String adminUser(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal, Model
            model) {
        User userToUpdate = userService.findById(id);

        Role roleAdmin = roleService.findByName(Roles.ADMIN);

        if (userToUpdate.getRoles().contains(roleAdmin)) {
            userToUpdate.removeRole(roleAdmin);

            User updatedUser = userService.save(userToUpdate);

            if (updatedUser != null) {
                model.addAttribute("user", updatedUser);

                //          update user session with changing roles!

                log.info("Admin with username: {} took away admin rights from user: {}",principal.getUsername(),updatedUser.getUsername());

                return PROFILE;
            } else {
                model.addAttribute("error", "Something went wrong!");

                return ERROR;
            }
        } else {
            userToUpdate.addRole(roleAdmin);

            User updatedUser = userService.save(userToUpdate);

            if (updatedUser != null) {
                model.addAttribute("user", updatedUser);

                //          update user session with changing roles!

                log.info("Admin with username: {} make user: {} to admin",principal.getUsername(),updatedUser.getUsername());

                return PROFILE;
            } else {
                model.addAttribute("error", "Something went wrong!");

                return ERROR;
            }
        }
    }
}
