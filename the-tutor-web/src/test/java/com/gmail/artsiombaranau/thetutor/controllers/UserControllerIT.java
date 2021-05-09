package com.gmail.artsiombaranau.thetutor.controllers;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.RoleService;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class UserControllerIT {

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    UserService userService;
    @MockBean
    RoleService roleService;

    @MockBean
    Model model;
    @MockBean
    BindingResult bindingResult;
    @MockBean
    UserDetailsImpl principal;

    @MockBean
    Collection<SimpleGrantedAuthority> authorities;

    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    SessionRegistry sessionRegistry;

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

        Role roleStudent = Role.builder().id(1L).name(Roles.STUDENT).build();
        Role roleAdmin = Role.builder().id(1L).name(Roles.ADMIN).build();

        User user = User.builder().id(1L).username("username")
                .encryptedPassword("4321")
                .email("user@mail.ru")
                .firstName("John")
                .lastName("Doe")
                .password("1234")
                .roles(List.of(roleStudent)).build();

        given(principal.getUsername()).willReturn("principal");
        given(principal.getAuthorities()).willReturn(List.of(new SimpleGrantedAuthority(Roles.ADMIN.name())));
        given(userService.findById(anyLong())).willReturn(user);
        given(userService.findByUsername(anyString())).willReturn(user);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userService.save(any(User.class))).willReturn(null);
    }

    @WithAnonymousUser
    @Test
    void getUserIsNotAuthorized() throws Exception {
        mockMvc.perform(get("/user/username"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void getUserIsAuthorized() throws Exception {
        mockMvc.perform(get("/user/username")
                .with(user(student)))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void updateUserFormIsNotAuthorized() throws Exception {
        mockMvc.perform(get("/user/update"))
                .andExpect(status().is3xxRedirection());
    }

    @WithAnonymousUser
    @Test
    void saveUpdatedUserIsNotAuthorized() throws Exception {
        mockMvc.perform(post("/user/update"))
                .andExpect(status().is3xxRedirection());
    }

    @WithAnonymousUser
    @Test
    void deleteUserIsNotAuthorized() throws Exception {
        mockMvc.perform(get("/user/1/delete"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void adminUserIsNotAdmin() throws Exception {
        mockMvc.perform(get("/user/1/admin")
                .with(user(tutor)))
                .andExpect(status().isForbidden());
    }
}