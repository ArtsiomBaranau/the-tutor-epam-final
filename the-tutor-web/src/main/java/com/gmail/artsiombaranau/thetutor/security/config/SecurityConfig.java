package com.gmail.artsiombaranau.thetutor.security.config;

import com.gmail.artsiombaranau.thetutor.security.session.CustomSessionInformationExpiredStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.*;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.util.ArrayList;
import java.util.List;

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
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(requests ->
                                requests
                                        .mvcMatchers(HttpMethod.GET, "", "/", "/index", "/webjars/**", "/css/**", "/images/**").permitAll()
                                        .mvcMatchers("/login**").permitAll()
                                        .mvcMatchers("/register*").permitAll()
                                        .mvcMatchers("/logout*").authenticated()
                                        .mvcMatchers("/user/{username}*").authenticated()
                                        .mvcMatchers("/user/update*").authenticated()
                                        .mvcMatchers(HttpMethod.GET, "/user/{id}/delete*").authenticated()
                                        .mvcMatchers("/user/{id}/admin*").hasAuthority("ADMIN")
                                        .mvcMatchers("/menu*").authenticated()
                                        .mvcMatchers(HttpMethod.GET, "/quiz/{id}}*").authenticated()
                                        .mvcMatchers(HttpMethod.POST, "/quiz/pass*").authenticated()
                                        .mvcMatchers(HttpMethod.GET, "/quiz/create/quantity").hasAnyAuthority("TUTOR", "ADMIN")
                                        .mvcMatchers("/quiz/create").hasAnyAuthority("TUTOR", "ADMIN")
                                        .mvcMatchers(HttpMethod.GET, "/quiz/{id}/update*").hasAnyAuthority("TUTOR", "ADMIN")
                                        .mvcMatchers(HttpMethod.POST, "/quiz/update*").hasAnyAuthority("TUTOR", "ADMIN")
                                        .mvcMatchers(HttpMethod.GET, "/quiz/{id}/delete*").hasAuthority("ADMIN")
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin(formLogin ->
                formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?failure=true")
        )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(sessionManagement ->
                                sessionManagement
                                        .sessionAuthenticationStrategy(concurrentSession())
                                        .sessionConcurrency(sessionConcurrency ->
                                                        sessionConcurrency
                                                                .maximumSessions(-1)
                                                                .sessionRegistry(sessionRegistry())
                                                                .expiredSessionStrategy(sessionInformationExpiredStrategy())
                                        )
                );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return this.authenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public CompositeSessionAuthenticationStrategy concurrentSession() {

        ConcurrentSessionControlAuthenticationStrategy concurrentAuthenticationStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();

        delegateStrategies.add(concurrentAuthenticationStrategy);
        delegateStrategies.add(new SessionFixationProtectionStrategy());
        delegateStrategies.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));

        return new CompositeSessionAuthenticationStrategy(delegateStrategies);
    }

    @Bean
    public SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new CustomSessionInformationExpiredStrategy("/login");
    }
}