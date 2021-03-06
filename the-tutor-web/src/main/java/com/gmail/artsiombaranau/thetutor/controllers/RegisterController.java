package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.EmailSenderService;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private static final String CREATE_OR_UPDATE = "user/create_or_update";
    private static final String REDIRECT_MENU = "redirect:/menu";

    private final UserService userService;
    private final RoleService roleService;

    private final EmailSenderService emailSenderService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Qualifier(value = "userToUserDetailsConverter")
    private final Converter<User, UserDetailsImpl> userToUserDetailsConverter;

    @GetMapping
    public String registerUserForm(Model model) {
        User user = User.builder().build();

        Role roleStudent = roleService.findByName(Roles.STUDENT);
        Role roleTutor = roleService.findByName(Roles.TUTOR);

        List<Role> roles = List.of(roleStudent, roleTutor);

        model.addAttribute("user", user);
        model.addAttribute("rolesList", roles);

        return CREATE_OR_UPDATE;
    }

    @PostMapping
    public String processUserCreationForm(@ModelAttribute @Valid User user, BindingResult bindingResult, HttpServletRequest httpRequest, Model model) {
        if (bindingResult.hasErrors()) {
            List<Role> roles = new ArrayList<>();
            roles.add(roleService.findByName(Roles.STUDENT));
            roles.add(roleService.findByName(Roles.TUTOR));

            model.addAttribute("user", user);
            model.addAttribute("rolesList", roles);

            return CREATE_OR_UPDATE;
        } else {
            boolean existsByUsername = userService.existsByUsername(user.getUsername());
            boolean existsByEmail = userService.existsByEmail(user.getEmail());

            if (existsByUsername || existsByEmail) {
                if (existsByUsername) {
                    String messageUsername = user.getUsername() + " already exists!";
                    bindingResult.addError(new FieldError("user", "username", messageUsername));
                }
                if (existsByEmail) {
                    String messageEmail = user.getEmail() + " already exists!";
                    bindingResult.addError(new FieldError("user", "email", messageEmail));
                }

                Role roleStudent = roleService.findByName(Roles.STUDENT);
                Role roleTutor = roleService.findByName(Roles.TUTOR);

                List<Role> roles = List.of(roleStudent, roleTutor);

                model.addAttribute("rolesList", roles);
                model.addAttribute("user", user);

                return CREATE_OR_UPDATE;
            } else {
                user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));
                User savedUser = userService.save(user);

//              auto login user after registration
                UserDetailsImpl userDetails = userToUserDetailsConverter.convert(savedUser);

                AbstractAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getUsername(),
                        user.getPassword(), userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetails(httpRequest));

                Authentication authentication = authenticationManager.authenticate(authToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);

//              send success registration message to email
                try {
                    emailSenderService.sendMail(savedUser);
                } catch (MailException | MessagingException ex) {
                    log.error("Some problem with JavaMailSender:" + ex.getCause(), ex);
                }

                log.info("User: {} just registered!", savedUser.getUsername());

                return REDIRECT_MENU;
            }
        }
    }
}
