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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String CREATE_OR_UPDATE = "user/create_or_update";
    private static final String PROFILE = "user/profile";
    private static final String REDIRECT_MENU = "redirect:/menu";
    private static final String REDIRECT_LOGOUT = "redirect:/logout";
    private static final String ERROR = "error";

    @Mock
    UserService userService;
    @Mock
    RoleService roleService;

    @Mock
    Model model;
    @Mock
    BindingResult bindingResult;
    @Mock
    UserDetailsImpl principal;
    @Mock
    Collection<SimpleGrantedAuthority> authorities;

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    SessionRegistry sessionRegistry;
    @Mock
    Converter<User, UserDetailsImpl> userToUserDetailsConverter;

    @InjectMocks
    UserController userController;

    User user;
    Role roleAdmin;
    Role roleStudent;
    List<Role> roles;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("username").email("user@mail.ru").password("password").firstName("John").lastName("Doe").build();

        roleStudent = Role.builder().id(1L).name(Roles.STUDENT).build();
        roleAdmin = Role.builder().id(1L).name(Roles.ADMIN).build();
        roles = List.of(roleStudent, roleAdmin);
    }

    @Test
    void getUserNotFound() {
//        given
        given(userService.findByUsername(anyString())).willReturn(null);
//        when
        String viewName = userController.getUser("username", principal, model);
//        then
        then(userService).should(times(1)).findByUsername(anyString());
        then(model).should(times(1)).addAttribute(anyString(), anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void getUserFoundPrincipalIsNotAdmin() {
//        given
        given(userService.findByUsername(anyString())).willReturn(user);
        given(principal.getAuthorities()).willReturn(authorities);
        given(authorities.contains(any(SimpleGrantedAuthority.class))).willReturn(false);
//        when
        String viewName = userController.getUser("username", principal, model);
//        then
        then(userService).should(times(1)).findByUsername(anyString());
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));

        assertEquals(PROFILE, viewName);
    }

    @Test
    void getUserFoundPrincipalIsAdmin() {
//        given
        given(userService.findByUsername(anyString())).willReturn(user);
        given(principal.getAuthorities()).willReturn(authorities);
        given(authorities.contains(any(SimpleGrantedAuthority.class))).willReturn(true);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
//        when
        String viewName = userController.getUser("username", principal, model);
//        then
        then(userService).should(times(1)).findByUsername(anyString());
        then(roleService).should(times(1)).findByName(any(Roles.class));
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(PROFILE, viewName);
    }

    @Test
    void updateUserForm() {
//        given
        given(principal.getUsername()).willReturn("username");
        given(userService.findByUsername(anyString())).willReturn(user);
//        when
        String viewName = userController.updateUserForm(principal, model);
//        then
        then(userService).should(times(1)).findByUsername(anyString());
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedUserHasErrors() {
//        given
        given(bindingResult.hasErrors()).willReturn(true);
//        when
        String viewName = userController.saveUpdatedUser(user, bindingResult, principal, model);
//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedUserHasNoErrorsUsersNameNotEqualsPrincipals() {
//        given
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.existsByUsername(anyString())).willReturn(true);
        given(principal.getUsername()).willReturn("username_not_equals");
//        when
        String viewName = userController.saveUpdatedUser(user, bindingResult, principal, model);
//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(bindingResult).should(times(1)).addError(any(FieldError.class));
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedUserHasNoErrorsUsersNameEqualsPrincipalsAndUserWasNotSaved() {
//        given
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.existsByUsername(anyString())).willReturn(true);
        given(principal.getUsername()).willReturn("username");
        given(passwordEncoder.encode(anyString())).willReturn("encoded password");
        given(userService.save(any(User.class))).willReturn(null);
//        when
        String viewName = userController.saveUpdatedUser(user, bindingResult, principal, model);
//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(userService).should(times(1)).existsByUsername(anyString());
        then(userService).should(times(1)).save(any(User.class));
        then(principal).should(times(1)).getUsername();
        then(passwordEncoder).should(times(1)).encode(anyString());
        then(model).should(times(2)).addAttribute(anyString(), any());

        assertEquals(CREATE_OR_UPDATE, viewName);
    }

    @Test
    void saveUpdatedUserHasNoErrorsUsersNameEqualsPrincipalsAndUserWasSaved() {
//        given
        given(bindingResult.hasErrors()).willReturn(false);
        given(userService.existsByUsername(anyString())).willReturn(true);
        given(principal.getUsername()).willReturn("username");
        given(passwordEncoder.encode(anyString())).willReturn("encoded password");
        given(userService.save(any(User.class))).willReturn(user);
        given(userToUserDetailsConverter.convert(any(User.class))).willReturn(principal);
//        when
        String viewName = userController.saveUpdatedUser(user, bindingResult, principal, model);
//        then
        then(bindingResult).should(times(1)).hasErrors();
        then(userService).should(times(1)).existsByUsername(anyString());
        then(userService).should(times(1)).save(any(User.class));
        then(principal).should(times(1)).getUsername();
        then(passwordEncoder).should(times(1)).encode(anyString());
        then(userToUserDetailsConverter).should(times(1)).convert(any(User.class));
        then(model).should(times(1)).addAttribute(anyString(), any(User.class));

        assertEquals(PROFILE, viewName);
    }

    @Test
    void deleteUserByHimself() {
//        given
        given(principal.getUsername()).willReturn("username");
//        given(userService.findByUsername(anyString())).willReturn(user);
        given(userService.findById(anyLong())).willReturn(user);
//        when
        String viewName = userController.deleteUser(1L, principal, model);
//        then
//        then(userService).should(times(1)).findByUsername(anyString());
        then(principal).should(times(1)).getUsername();
        then(userService).should(times(1)).findById(anyLong());

        assertEquals(REDIRECT_LOGOUT, viewName);
    }

    @Test
    void deleteUserByAdminPrincipalIsAdmin() {
//        given
        given(principal.getUsername()).willReturn("username_admin");
        given(userService.findById(anyLong())).willReturn(user);
        given(principal.getAuthorities()).willReturn(List.of(new SimpleGrantedAuthority(Roles.ADMIN.name())));
        UserDetailsImpl loggedUser = Mockito.mock(UserDetailsImpl.class);
        given(sessionRegistry.getAllPrincipals()).willReturn(List.of(loggedUser));
        given(loggedUser.getUsername()).willReturn("username");
        SessionInformation sessionInformation = Mockito.mock(SessionInformation.class);
        given(sessionRegistry.getAllSessions(loggedUser, false)).willReturn(List.of(sessionInformation));
//        when
        String viewName = userController.deleteUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(principal).should(times(2)).getUsername();
        then(principal).should(times(1)).getAuthorities();
        then(userService).should(times(1)).deleteById(anyLong());
        then(sessionRegistry).should(times(1)).getAllPrincipals();
        then(sessionRegistry).should(times(1)).getAllSessions(any(), anyBoolean());
        then(sessionInformation).should(atLeastOnce()).expireNow();

        assertEquals(REDIRECT_MENU, viewName);
    }

    @Test
    void deleteUserByAdminPrincipalIsNotAdmin() {
//        given
        given(principal.getUsername()).willReturn("username_tutor");
        given(userService.findById(anyLong())).willReturn(user);
        given(principal.getAuthorities()).willReturn(List.of(new SimpleGrantedAuthority(Roles.TUTOR.name())));
//        when
        String viewName = userController.deleteUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(principal).should(times(1)).getUsername();
        then(principal).should(times(1)).getAuthorities();
        then(model).should(times(1)).addAttribute(anyString(), anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void makeUserToAdminNotSaved() {
//        given
        User userToUpdate = Mockito.mock(User.class);
        given(userService.findById(anyLong())).willReturn(userToUpdate);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userToUpdate.getRoles()).willReturn(List.of(roleStudent));
        given(userService.save(any(User.class))).willReturn(null);
//        when
        String viewName = userController.adminUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(roleService).should(times(1)).findByName(any(Roles.class));
        then(userToUpdate).should(times(1)).getRoles();
        then(userToUpdate).should(times(1)).addRole(any(Role.class));
        then(model).should(times(1)).addAttribute(anyString(),anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void makeUserToAdminSaved() {
//        given
        User userToUpdate = Mockito.mock(User.class);
        given(userService.findById(anyLong())).willReturn(userToUpdate);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userToUpdate.getRoles()).willReturn(List.of(roleStudent));
        given(userService.save(any(User.class))).willReturn(user);
        given(userService.findByUsername(anyString())).willReturn(userToUpdate);
        given(principal.getAuthorities()).willReturn(authorities);
        given(authorities.contains(any(SimpleGrantedAuthority.class))).willReturn(false);
//        when
        String viewName = userController.adminUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(roleService).should(times(1)).findByName(any(Roles.class));
        then(userToUpdate).should(times(1)).getRoles();
        then(userToUpdate).should(times(1)).addRole(any(Role.class));
        then(model).should(times(1)).addAttribute(anyString(),any(User.class));

        assertEquals(PROFILE, viewName);
    }

    @Test
    void takeAwayAdminRightsFromUserNotSaved() {
//        given
        User userToUpdate = Mockito.mock(User.class);
        given(userService.findById(anyLong())).willReturn(userToUpdate);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userToUpdate.getRoles()).willReturn(List.of(roleAdmin));
        given(userService.save(any(User.class))).willReturn(null);
//        when
        String viewName = userController.adminUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(roleService).should(times(1)).findByName(any(Roles.class));
        then(userToUpdate).should(times(1)).getRoles();
        then(userToUpdate).should(times(1)).removeRole(any(Role.class));
        then(model).should(times(1)).addAttribute(anyString(),anyString());

        assertEquals(ERROR, viewName);
    }

    @Test
    void takeAwayAdminRightsFromUserSaved() {
//        given
        User userToUpdate = Mockito.mock(User.class);
        given(userService.findById(anyLong())).willReturn(userToUpdate);
        given(roleService.findByName(any(Roles.class))).willReturn(roleAdmin);
        given(userToUpdate.getRoles()).willReturn(List.of(roleAdmin));
        given(userService.save(any(User.class))).willReturn(user);
        given(userService.findByUsername(anyString())).willReturn(userToUpdate);
        given(principal.getAuthorities()).willReturn(authorities);
        given(authorities.contains(any(SimpleGrantedAuthority.class))).willReturn(false);
//        when
        String viewName = userController.adminUser(1L, principal, model);
//        then
        then(userService).should(times(1)).findById(anyLong());
        then(roleService).should(times(1)).findByName(any(Roles.class));
        then(userToUpdate).should(times(1)).getRoles();
        then(userToUpdate).should(times(1)).removeRole(any(Role.class));
        then(model).should(times(1)).addAttribute(anyString(),any(User.class));

        assertEquals(PROFILE, viewName);
    }
}