package com.gmail.artsiombaranau.thetutor.bootstrap;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.enums.Specialties;
import com.gmail.artsiombaranau.thetutor.model.*;
import com.gmail.artsiombaranau.thetutor.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    private final QuizService quizService;
    private final RoleService roleService;
    private final SpecialtyService specialtyService;
    private final UserService userService;

    @Override
    public void run(String... args) {
//        loadData();
    }

    private void loadData() {
        Role studentRole = roleService.save(Role.builder().name(Roles.STUDENT).build());
        Role tutorRole = roleService.save(Role.builder().name(Roles.TUTOR).build());
        Role adminRole = roleService.save(Role.builder().name(Roles.ADMIN).build());

        log.info("Roles are loaded.");

        Specialty chemistrySpecialty = specialtyService.save(Specialty.builder().description(Specialties.CHEMISTRY).build());
        Specialty physicsSpecialty = specialtyService.save(Specialty.builder().description(Specialties.PHYSICS).build());
        Specialty mathSpecialty = specialtyService.save(Specialty.builder().description(Specialties.MATH).build());
        Specialty biologySpecialty = specialtyService.save(Specialty.builder().description(Specialties.BIOLOGY).build());
        Specialty psychologySpecialty = specialtyService.save(Specialty.builder().description(Specialties.PSYCHOLOGY).build());
        Specialty sociologySpecialty = specialtyService.save(Specialty.builder().description(Specialties.SOCIOLOGY).build());
        Specialty astronomySpecialty = specialtyService.save(Specialty.builder().description(Specialties.ASTRONOMY).build());
        Specialty economicsSpecialty = specialtyService.save(Specialty.builder().description(Specialties.ECONOMICS).build());
        Specialty philosophySpecialty = specialtyService.save(Specialty.builder().description(Specialties.PHILOSOPHY).build());
        Specialty computingSpecialty = specialtyService.save(Specialty.builder().description(Specialties.COMPUTING).build());

        log.info("Specialties are loaded.");

        User adminUser = User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password("admin")
                .encryptedPassword(passwordEncoder.encode("admin"))
                .roles(List.of(tutorRole,adminRole))
                .firstName("John")
                .lastName("Thompson")
                .build();

        userService.save(adminUser);

        User tutorUser = User.builder()
                .username("tutor")
                .email("tutor@gmail.com")
                .password("tutor")
                .encryptedPassword(passwordEncoder.encode("tutor"))
                .roles(List.of(tutorRole))
                .firstName("John")
                .lastName("Doe")
                .build();

        userService.save(tutorUser);

        User studentUser = User.builder()
                .username("student")
                .email("student@gmail.com")
                .password("student")
                .encryptedPassword(passwordEncoder.encode("student"))
                .roles(List.of(studentRole))
                .firstName("Jane")
                .lastName("Doe")
                .build();

        userService.save(studentUser);

        log.info("Users are loaded.");

        Quiz quizOne = Quiz.builder().name("Math quiz.").description("Easy quiz for children...").specialty(mathSpecialty).user(adminUser).build();

        List<Question> questionsQuizOne = new ArrayList<>();

        Question questionOne = Question.builder().description("2x4?").quiz(quizOne).build();

        List<Answer> answersQuestionOne = new ArrayList<>();

        answersQuestionOne.add(Answer.builder().description("2").isRight(false).question(questionOne).build());
        answersQuestionOne.add(Answer.builder().description("4").isRight(false).question(questionOne).build());
        answersQuestionOne.add(Answer.builder().description("6").isRight(false).question(questionOne).build());
        answersQuestionOne.add(Answer.builder().description("8").isRight(true).question(questionOne).build());

        questionOne.setAnswers(answersQuestionOne);

        questionsQuizOne.add(questionOne);

        Question questionTwo = Question.builder().description("3+3?").quiz(quizOne).build();

        List<Answer> answersQuestionTwo = new ArrayList<>();

        answersQuestionTwo.add(Answer.builder().description("2").isRight(false).question(questionTwo).build());
        answersQuestionTwo.add(Answer.builder().description("4").isRight(false).question(questionTwo).build());
        answersQuestionTwo.add(Answer.builder().description("6").isRight(true).question(questionTwo).build());
        answersQuestionTwo.add(Answer.builder().description("8").isRight(false).question(questionTwo).build());

        questionTwo.setAnswers(answersQuestionTwo);

        questionsQuizOne.add(questionTwo);

        Question questionThree = Question.builder().description("7 _ 10?").quiz(quizOne).build();

        List<Answer> answersQuestionThree = new ArrayList<>();

        answersQuestionThree.add(Answer.builder().description("<").isRight(true).question(questionThree).build());
        answersQuestionThree.add(Answer.builder().description("<=").isRight(true).question(questionThree).build());
        answersQuestionThree.add(Answer.builder().description(">").isRight(false).question(questionThree).build());
        answersQuestionThree.add(Answer.builder().description("=").isRight(false).question(questionThree).build());

        questionThree.setAnswers(answersQuestionThree);

        questionsQuizOne.add(questionThree);

        quizOne.setQuestions(questionsQuizOne);

        quizService.save(quizOne);

        log.info("Quizzes are loaded.");
    }
}
