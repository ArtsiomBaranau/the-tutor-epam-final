package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    @Mock
    QuizService quizService;
    @Mock
    UserService userService;
    @Mock
    RoleService roleService;

    @Mock
    Model model;
    @Mock
    UserDetailsImpl principal;
    @Mock
    Collection<SimpleGrantedAuthority> authorities;

    @InjectMocks
    MenuController menuController;

    Role roleAdmin;
    List<User> users;
    List<Quiz> quizzes;

    @BeforeEach
    void setUp() {
        roleAdmin = Role.builder().id(1L).name(Roles.ADMIN).build();
        users = List.of(User.builder().id(1L).build());
        quizzes = List.of(Quiz.builder().id(1L).build());

        given(quizService.findAll()).willReturn(quizzes);
    }

    @Test
    void getAdminMenu() {
//        given
        doReturn(authorities).when(principal).getAuthorities();
        doReturn(true).when(authorities).contains(any(SimpleGrantedAuthority.class));
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userService.findAll()).willReturn(users);
//        when
        String viewName = menuController.getMenu(principal, model);
//        then
        then(model).should(times(3)).addAttribute(anyString(), any());
        assertEquals(viewName, "menu");
    }

    @Test
    void getUserMenu() {
//        given
        doReturn(authorities).when(principal).getAuthorities();
        doReturn(false).when(authorities).contains(any(SimpleGrantedAuthority.class));
//        when
        String viewName = menuController.getMenu(principal, model);
//        then
        then(model).should(times(1)).addAttribute(anyString(), anyList());
        assertEquals(viewName, "menu");
    }
}