package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.model.Question;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.QuizService;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import com.gmail.artsiombaranau.thetutor.utils.QuizUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
class QuizControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    UserService userService;
    @MockBean
    QuizService quizService;
    @MockBean
    SpecialtyService specialtyService;
    @MockBean
    QuizUtils quizUtils;

    @MockBean
    Quiz quiz;

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
    void createQuizFormIsNotAuthorized() throws Exception {
        mockMvc.perform(get("/quiz/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void createQuizFormIsAuthorizedStudent() throws Exception {
        mockMvc.perform(get("/quiz/create")
                .with(user(student)))
                .andExpect(status().isForbidden());
    }

    @WithAnonymousUser
    @Test
    void saveQuizIsNotAuthorized() throws Exception {
        mockMvc.perform(post("/quiz/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void saveQuizIsAuthorizedStudent() throws Exception {
        mockMvc.perform(post("/quiz/create")
                .with(user(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveQuizIsAuthorizedTutor() throws Exception {
        mockMvc.perform(post("/quiz/create")
                .with(user(tutor)))
                .andExpect(status().isOk());
    }

    @Test
    void updateQuizFormIsStudent() throws Exception {
        mockMvc.perform(get("/quiz/1/update")
                .with(user(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateQuizFormIsTutor() throws Exception {
        mockMvc.perform(get("/quiz/1/update")
                .with(user(tutor)))
                .andExpect(status().isOk());
    }

    @Test
    void saveUpdatedQuizIsStudent() throws Exception {
        mockMvc.perform(post("/quiz//update")
                .with(user(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveUpdatedQuizIsTutor() throws Exception {
        mockMvc.perform(post("/quiz//update")
                .with(user(tutor)))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void getQuizIsNotAuthorized() throws Exception {
        mockMvc.perform(get("/quiz/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getQuizIsStudent() throws Exception {
        mockMvc.perform(get("/quiz/1")
                .with(user(student)))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void passQuizIsNotAuthorized() throws Exception {
        mockMvc.perform(post("/quiz/pass"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void deleteQuizIsStudent() throws Exception {
        mockMvc.perform(get("/quiz/1/delete")
                .with(user(student)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteQuizIsAdmin() throws Exception {
        mockMvc.perform(get("/quiz/1/delete")
                .with(user(admin)))
                .andExpect(status().is3xxRedirection());
    }
}