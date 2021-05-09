package com.gmail.artsiombaranau.thetutor.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @InjectMocks
    IndexController indexController;

    @Test
    void getIndexPageWithSlashURL() {
        String viewName = indexController.getIndexPage();

        assertEquals(viewName, "index");
    }
}