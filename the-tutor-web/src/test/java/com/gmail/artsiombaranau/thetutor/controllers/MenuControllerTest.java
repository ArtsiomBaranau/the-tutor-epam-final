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
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

    private static final String MENU = "menu";

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
    @Mock
    Page<User> pageUser;
    @Mock
    Page<Quiz> pageQuiz;

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

        doReturn(authorities).when(principal).getAuthorities();
    }

    @Test
    void getMenu() {
//        given
        doReturn(true).when(authorities).contains(any(SimpleGrantedAuthority.class));
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userService.findPaginated(anyInt(), anyInt())).willReturn(pageUser);
        given(pageUser.getContent()).willReturn(users);
//        when
        String viewName = menuController.getMenu(principal, model);
//        then
        assertEquals(MENU, viewName);
    }

    @Test
    void getUserMenuPage() {
//        given
        doReturn(false).when(authorities).contains(any(SimpleGrantedAuthority.class));
        given(quizService.findPaginated(anyInt(), anyInt())).willReturn(pageQuiz);
        given(pageQuiz.getContent()).willReturn(quizzes);
//        when
        String viewName = menuController.getMenuPage(principal, 1, model);
//        then
        then(model).should(times(4)).addAttribute(anyString(), any());

        assertEquals(MENU, viewName);
    }

    @Test
    void getAdminMenuPage() {
//        given
        doReturn(true).when(authorities).contains(any(SimpleGrantedAuthority.class));
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userService.findPaginated(anyInt(), anyInt())).willReturn(pageUser);
        given(pageUser.getContent()).willReturn(users);
//        when
        String viewName = menuController.getMenu(principal, model);
//        then
        then(model).should(times(5)).addAttribute(anyString(), any());

        assertEquals(MENU, viewName);
    }
}