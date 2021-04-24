package com.gmail.artsiombaranau.thetutor.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    UserDetailsService userDetailsService;

    @GetMapping
    public String loginForm() {

        return "login";
    }

    @PostMapping
    public String doLogin() {
//        try {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
//        } catch (UsernameNotFoundException ex) {
//            log.error("Username doesn't exist!", ex);
//        }
        //check passwords

        return "index";
    }
}
