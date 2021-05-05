package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    RoleServiceImpl roleService;

    Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder().id(1L).name(Roles.STUDENT).build();
    }

    @Test
    void findAll() {
//      given
        List<Role> roles = List.of(role);
        given(roleRepository.findAll()).willReturn(roles);
//      when
        Iterable<Role> receivedRoles = roleService.findAll();
//      then
        then(roleRepository).should(times(1)).findAll();
        assertIterableEquals(roles, receivedRoles);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findByNameNotFound(){
//        given
        given(roleRepository.findByName(any(Roles.class))).willReturn(Optional.empty());
//        when
        Role returnedRole = roleService.findByName(Roles.STUDENT);
//        then
        then(roleRepository).should(times(1)).findByName(any(Roles.class));
        assertNull(returnedRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findByNameFound(){
//        given
        given(roleRepository.findByName(any(Roles.class))).willReturn(Optional.of(role));
//        when
        Role returnedRole = roleService.findByName(Roles.STUDENT);
//        then
        then(roleRepository).should(times(1)).findByName(any(Roles.class));
        assertNotNull(returnedRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(roleRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        Role returnedRole = roleService.findById(1L);
//        then
        then(roleRepository).should(times(1)).findById(anyLong());
        assertNull(returnedRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(roleRepository.findById(anyLong())).willReturn(Optional.of(role));
//        when
        Role returnedRole = roleService.findById(1L);
//        then
        then(roleRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void save() {
//        given
        given(roleRepository.save(any(Role.class))).willReturn(role);
//        when
        Role savedRole = roleService.save(role);
//        then
        then(roleRepository).should(times(1)).save(any(Role.class));
        assertNotNull(savedRole);
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void delete() {
//        when
        roleService.delete(role);
//        then
        then(roleRepository).should(times(1)).delete(any(Role.class));
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void deleteById() {
//        when
        roleService.deleteById(1L);
//        then
        then(roleRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(roleRepository);
    }
}