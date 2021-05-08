package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class MenuControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    QuizService quizService;
    @MockBean
    UserService userService;
    @MockBean
    RoleService roleService;

    MockMvc mockMvc;

    UserDetails student;
    UserDetails tutor;
    UserDetails admin;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        student = UserDetailsImpl.builder().authorities(List.of(new SimpleGrantedAuthority(Roles.STUDENT.name()))).build();
        tutor = UserDetailsImpl.builder().authorities(List.of(new SimpleGrantedAuthority(Roles.TUTOR.name()))).build();
        admin = UserDetailsImpl.builder().authorities(List.of(new SimpleGrantedAuthority(Roles.ADMIN.name()))).build();
    }

    @WithAnonymousUser
    @Test
    void getMenuNotAuthorized() throws Exception {
        mockMvc.perform(get("/menu"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    void getMenuAuthorizedStudent() throws Exception {
        mockMvc.perform(get("/menu")
                .with(user(student)))
                .andExpect(status().isOk())
                .andExpect(view().name("menu"));
    }

    @Test
    void getMenuAuthorizedAdmin() throws Exception {
        mockMvc.perform(get("/menu")
                .with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("menu"));
    }
}