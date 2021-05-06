package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    private static final String CREATE_OR_UPDATE = "user/create_or_update";
    private static final String REDIRECT_MENU = "redirect:/menu";

    @Mock
    BindingResult bindingResult;
    @Mock
    HttpServletRequest request;
    @Mock
    Model model;
    @Mock
    UserService userService;
    @Mock
    RoleService roleService;
    @Mock
    JavaMailSender javaMailSender;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    Converter<User, UserDetailsImpl> userToUserDetailsConverter;
    @Mock
    UserDetailsImpl userDetails;

    @InjectMocks
    RegisterController controller;

    User user;
    Role roleStudent;
    Role roleTutor;
    List<Role> roles;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("username").email("user@mail.ru").password("password").firstName("John").lastName("Doe").build();

        roleStudent = Role.builder().id(1L).name(Roles.STUDENT).build();
        roleTutor = Role.builder().id(2L).name(Roles.TUTOR).build();

        roles = List.of(roleStudent, roleTutor);
    }

    @Test
    void registerUserForm() {
//        given
        doReturn(roleStudent).when(roleService).findByName(any(Roles.class));
//        when
        String viewName = controller.registerUserForm(model);
//        then
        then(roleService).should(times(2)).findByName(any(Roles.class));
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));
        then(model).should(times(1)).addAttribute(anyString(), anyList());

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void processUserCreationFormHasErrors() {
//      given
        given(bindingResult.hasErrors()).willReturn(true);
        InOrder inOrder = Mockito.inOrder(bindingResult, model);
//      when
        String viewName = controller.processUserCreationForm(user, bindingResult, request, model);
//      then
        assertEquals(CREATE_OR_UPDATE, viewName);
//      in order asserts
        inOrder.verify(bindingResult).hasErrors();
        inOrder.verify(model).addAttribute(anyString(), any(User.class));
//        inOrder.verify(model, times(2)).addAttribute(anyString(), any());
        inOrder.verify(model).addAttribute(anyString(), anyList());

        verifyNoMoreInteractions(model);
        verifyNoInteractions(userService);
        verifyNoInteractions(request);
    }

    @Test
    void processFormHasNoErrorsAndEmailAndUsernameExists() {
//      given
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.existsByUsername(anyString())).willReturn(true);
        given(userService.existsByEmail(anyString())).willReturn(true);
        given(roleService.findByName(any(Roles.class))).willReturn(roleStudent);
//      when
        String viewName = controller.processUserCreationForm(user, bindingResult, request, model);
//      then
        then(bindingResult).should(times(1)).hasErrors();
        then(bindingResult).should(times(2)).addError(any(FieldError.class));
        then(userService).should(times(1)).existsByUsername(anyString());
        then(userService).should(times(1)).existsByEmail(anyString());
        then(roleService).should(times(2)).findByName(any(Roles.class));
        then(model).should(times(2)).addAttribute(anyString(), any());
        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void processFormHasNoErrorsAndEmailAndUsernameDoNotExist() {
//      given
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.existsByUsername(anyString())).willReturn(false);
        given(userService.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encoded password");
        given(userService.save(any(User.class))).willReturn(user);
        given(userToUserDetailsConverter.convert(any(User.class))).willReturn(userDetails);
        given(userDetails.getAuthorities()).willReturn(List.of(new SimpleGrantedAuthority(Roles.STUDENT.name())));
        given(authenticationManager.authenticate(any())).willReturn(Mockito.mock(Authentication.class));
//      when
        String viewName = controller.processUserCreationForm(user, bindingResult, request, model);
//      then
        then(bindingResult).should(times(1)).hasErrors();
        then(userService).should(times(1)).existsByUsername(anyString());
        then(userService).should(times(1)).existsByEmail(anyString());
        then(passwordEncoder).should(times(1)).encode(anyString());
        then(userService).should(times(1)).save(any(User.class));
        then(userToUserDetailsConverter).should(times(1)).convert(any(User.class));
        then(userDetails).should(times(1)).getAuthorities();
        then(authenticationManager).should(times(1)).authenticate(any());
        then(javaMailSender).should(times(1)).send(any(SimpleMailMessage.class));

        assertEquals(REDIRECT_MENU, viewName);
    }
}