package com.gmail.artsiombaranau.thetutor.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception { // use db authentication with our user details service impl
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception { // allow to use h2-console
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(requests ->
//                                requests.anyRequest().permitAll()
                                requests
                                        .mvcMatchers(HttpMethod.GET, "", "/", "/index", "/css/**", "/js/**", "/images/**").permitAll()
                                        .mvcMatchers(HttpMethod.GET, "/login").permitAll()
                                        .mvcMatchers("/user/create").permitAll()
                                        .mvcMatchers("/user/update").authenticated()
                                        .mvcMatchers(HttpMethod.GET, "/quiz/{id}}").authenticated()
                                        .mvcMatchers("/quiz/create").hasAnyRole("TUTOR", "ADMIN")
                                        .mvcMatchers(HttpMethod.GET, "/quiz/{id}/update").hasAnyRole("TUTOR", "ADMIN")
                                        .mvcMatchers(HttpMethod.POST, "/quiz/update").hasAnyRole("TUTOR", "ADMIN")
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/index").invalidateHttpSession(true);
//                .and().httpBasic();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}