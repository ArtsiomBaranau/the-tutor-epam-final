package com.gmail.artsiombaranau.thetutor.security.service;

import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import com.gmail.artsiombaranau.thetutor.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    User user;

    @Mock
    UserDetailsImpl userDetails;

    @Mock
    UserService userService;

    @Mock
    Converter<User, UserDetailsImpl> userToUserDetailsConverter;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;


    @Test
    void loadUserByUsernameUserFound() {
//        given
        given(userService.findByUsername(anyString())).willReturn(user);
        given(userToUserDetailsConverter.convert(any(User.class))).willReturn(userDetails);
//        when
        UserDetailsImpl userDetails = userDetailsService.loadUserByUsername("username");
//        then
        then(userService).should(times(1)).findByUsername(anyString());
        then(userToUserDetailsConverter).should(times(1)).convert(any(User.class));

        assertNotNull(userDetails);
    }

    @Test
    void loadUserByUsernameUserNotFound() {
//        given
        given(userService.findByUsername(anyString())).willReturn(null);
//        when
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("username");
        });
//        then
        then(userService).should(times(1)).findByUsername(anyString());
    }
}