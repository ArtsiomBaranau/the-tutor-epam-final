package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.enums.Specialties;
import com.gmail.artsiombaranau.thetutor.model.Specialty;
import com.gmail.artsiombaranau.thetutor.repositories.SpecialtyRepository;
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
class SpecialtyServiceImplTest {

    @Mock
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialtyServiceImpl specialtyService;

    Specialty specialty;

    @BeforeEach
    void setUp() {
       specialty = Specialty.builder().id(1L).description(Specialties.MATH).build();
    }

    @Test
    void findAll() {
//      given
        List<Specialty> specialties = List.of(specialty);
        given(specialtyRepository.findAll()).willReturn(specialties);
//      when
        Iterable<Specialty> receivedSpecialties = specialtyService.findAll();
//      then
        then(specialtyRepository).should(times(1)).findAll();
        assertIterableEquals(specialties, receivedSpecialties);
        verifyNoMoreInteractions(specialtyRepository);
    }

    @Test
    void findByIdNotFound() {
//        given
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.empty());
//        when
        Specialty returnedSpecialty = specialtyService.findById(1L);
//        then
        then(specialtyRepository).should(times(1)).findById(anyLong());
        assertNull(returnedSpecialty);
        verifyNoMoreInteractions(specialtyRepository);
    }

    @Test
    void findByIdFound() {
//        given
        given(specialtyRepository.findById(anyLong())).willReturn(Optional.of(specialty));
//        when
        Specialty returnedSpecialty = specialtyService.findById(1L);
//        then
        then(specialtyRepository).should(times(1)).findById(anyLong());
        assertNotNull(returnedSpecialty);
        verifyNoMoreInteractions(specialtyRepository);
    }

    @Test
    void save() {
//        given
        given(specialtyRepository.save(any(Specialty.class))).willReturn(specialty);
//        when
        Specialty savedSpecialty = specialtyService.save(specialty);
//        then
        then(specialtyRepository).should(times(1)).save(any(Specialty.class));
        assertNotNull(savedSpecialty);
        verifyNoMoreInteractions(specialtyRepository);
    }

    @Test
    void delete() {
//        when
        specialtyService.delete(specialty);
//        then
        then(specialtyRepository).should(times(1)).delete(any(Specialty.class));
        verifyNoMoreInteractions(specialtyRepository);
    }

    @Test
    void deleteById() {
//        when
        specialtyService.deleteById(1L);
//        then
        then(specialtyRepository).should(times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(specialtyRepository);
    }

}