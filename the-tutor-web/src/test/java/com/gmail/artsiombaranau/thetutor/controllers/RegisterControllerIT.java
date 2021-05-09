package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class RegisterControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    UserService userService;
    @MockBean
    RoleService roleService;
    @MockBean
    Model model;
    @MockBean
    JavaMailSender javaMailSender;
    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    PasswordEncoder passwordEncoder;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        given(roleService.findByName(any())).willReturn(Role.builder().id(1L).build());
    }

    @WithAnonymousUser
    @Test
    void registerUserForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void processUserCreationForm() throws Exception {
        mockMvc.perform(post("/register"))
                .andExpect(status().isOk());
    }
}