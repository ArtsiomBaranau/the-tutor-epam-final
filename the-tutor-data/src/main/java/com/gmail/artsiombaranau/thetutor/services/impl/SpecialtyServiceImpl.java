package com.gmail.artsiombaranau.thetutor.services.impl;

import com.gmail.artsiombaranau.thetutor.model.Specialty;
import com.gmail.artsiombaranau.thetutor.repositories.SpecialtyRepository;
import com.gmail.artsiombaranau.thetutor.services.SpecialtyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialty> findAll() {
        List<Specialty> specialties = new ArrayList<>();
        specialtyRepository.findAll().forEach(specialties::add);
        return specialties;
    }

    @Override
    @Transactional(readOnly = true)
    public Specialty findById(Long id) {
        return specialtyRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public Specialty save(Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void delete(Specialty specialty) {
        specialtyRepository.delete(specialty);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, noRollbackFor = EntityNotFoundException.class)
    public void deleteById(Long id) {
        specialtyRepository.deleteById(id);
    }
}
