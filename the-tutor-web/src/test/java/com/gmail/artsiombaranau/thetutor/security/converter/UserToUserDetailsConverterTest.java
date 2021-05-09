package com.gmail.artsiombaranau.thetutor.security.converter;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;
import com.gmail.artsiombaranau.thetutor.model.User;
import com.gmail.artsiombaranau.thetutor.security.model.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserToUserDetailsConverterTest {

    @InjectMocks
    UserToUserDetailsConverter userToUserDetailsConverter;

    @Test
    void convert() {
//        given
        User originalUser = User.builder()
                .id(1L)
                .username("Username")
                .password("Password")
                .encryptedPassword("Encrypted Password")
                .email("user@mail.ru")
                .firstName("John")
                .lastName("Doe")
                .roles(List.of(Role.builder().name(Roles.STUDENT).build()))
                .build();

        UserDetailsImpl expectedUserDetails = UserDetailsImpl.builder()
                .username("Username")
                .password("Encrypted Password")
                .authorities(Set.of(new SimpleGrantedAuthority(Roles.STUDENT.name())))
                .build();
//        when
        UserDetailsImpl actualUserDetails = userToUserDetailsConverter.convert(originalUser);
//        then
        assertEquals(expectedUserDetails, actualUserDetails);
    }
}