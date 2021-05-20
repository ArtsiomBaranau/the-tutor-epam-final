package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("Username").password("Password").build();
    }

    @Test
    void findAll() {
//      given
        List<User> users = List.of(user);
        given(userRepository.findAll()).willReturn(users);
//      when
        Iterable<User> receivedUsers = userService.findAll();
//      then
        then(userRepository).should(times(1)).findAll();
        assertIterableEquals(users, receivedUsers);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findPaginated() {
//      given
        Page<User> page = Mockito.mock(Page.class);
        given(userRepository.findAll(any(Pageable.class))).willReturn(page);
//      when
        Page<User> receivedPage = userService.findPaginated(1,3);
//      then
        then(userRepository).should(times(1)).findAll(any(Pageable.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByNameNotFound(){
//        given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());
//        when
        User returnedUser = userService.findByUsername("Username");
//        then
        then(userRepository).should(times(1)).findByUsername(anyString());
        assertNull(returnedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByNameFound(){
//        given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
//        when
        User returnedUser = userService.findByUsername("Username");
//        then
        then(userRepository).should(times(1)).findByUsername(anyString());
        assertNotNull(returnedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        User returnedUser = userService.findById(1L);
//        then
        then(userRepository).should(times(1)).findById(anyLong());
        assertNull(returnedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
//        when
        User returnedUser = userService.findById(1L);
//        then
        then(userRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByUsernameTrue(){
//        given
        given(userRepository.existsByUsername(anyString())).willReturn(true);
//        when
        boolean exists = userService.existsByUsername("Username");
//        then
        then(userRepository).should(times(1)).existsByUsername(anyString());
        assertTrue(exists);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByUsernameFalse(){
//        given
        given(userRepository.existsByUsername(anyString())).willReturn(false);
//        when
        boolean exists = userService.existsByUsername("Username");
//        then
        then(userRepository).should(times(1)).existsByUsername(anyString());
        assertFalse(exists);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByEmailTrue(){
//        given
        given(userRepository.existsByEmail(anyString())).willReturn(true);
//        when
        boolean exists = userService.existsByEmail("user@mail.ru");
//        then
        then(userRepository).should(times(1)).existsByEmail(anyString());
        assertTrue(exists);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByEmailFalse(){
//        given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
//        when
        boolean exists = userService.existsByEmail("user@mail.ru");
//        then
        then(userRepository).should(times(1)).existsByEmail(anyString());
        assertFalse(exists);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void save() {
//        given
        given(userRepository.save(any(User.class))).willReturn(user);
//        when
        User savedUser = userService.save(user);
//        then
        then(userRepository).should(times(1)).save(any(User.class));
        assertNotNull(savedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void delete() {
//        when
        userService.delete(user);
//        then
        then(userRepository).should(times(1)).delete(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteById() {
//        when
        userService.deleteById(1L);
//        then
        then(userRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

}