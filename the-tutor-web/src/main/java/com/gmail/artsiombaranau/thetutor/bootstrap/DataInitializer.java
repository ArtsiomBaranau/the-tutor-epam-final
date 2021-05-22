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
        if (roleService.findAll().isEmpty())
            loadData();
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
                .roles(List.of(adminRole))
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

        Quiz quizOne = Quiz.builder().name("Math quiz.").description("Easy quiz for children...").specialty(mathSpecialty).user(tutorUser).build();

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

        Quiz quizTwo = Quiz.builder().name("Biology quiz.").description("Awesome biology quiz...").specialty(biologySpecialty).user(tutorUser).build();

        List<Question> questionsQuizTwo = new ArrayList<>();

        Question questionOneQuizTwo = Question.builder().description("How long does it take our eyes to fully adapt to darkness?").quiz(quizTwo).build();

        List<Answer> answersQuestionOneQuizTwo = new ArrayList<>();

        answersQuestionOneQuizTwo.add(Answer.builder().description("in 10 minutes").isRight(true).question(questionOneQuizTwo).build());
        answersQuestionOneQuizTwo.add(Answer.builder().description("in 15 minutes").isRight(false).question(questionOneQuizTwo).build());
        answersQuestionOneQuizTwo.add(Answer.builder().description("in 30 minutes").isRight(false).question(questionOneQuizTwo).build());
        answersQuestionOneQuizTwo.add(Answer.builder().description("in 2 hours").isRight(false).question(questionOneQuizTwo).build());

        questionOneQuizTwo.setAnswers(answersQuestionOneQuizTwo);

        questionsQuizTwo.add(questionOneQuizTwo);

        quizTwo.setQuestions(questionsQuizTwo);

        quizService.save(quizTwo);

        Quiz quizThree = Quiz.builder().name("Physics quiz.").description("Some physics quiz...").specialty(physicsSpecialty).user(tutorUser).build();

        List<Question> questionsQuizThree = new ArrayList<>();

        Question questionOneQuizThree = Question.builder().description("The instrument that measures and records the relative humidity of air is ...?").quiz(quizThree).build();

        List<Answer> answersQuestionOneQuizThree = new ArrayList<>();

        answersQuestionOneQuizThree.add(Answer.builder().description("Hydrometer").isRight(false).question(questionOneQuizThree).build());
        answersQuestionOneQuizThree.add(Answer.builder().description("Hygrometer").isRight(true).question(questionOneQuizThree).build());
        answersQuestionOneQuizThree.add(Answer.builder().description("Lactometer").isRight(false).question(questionOneQuizThree).build());
        answersQuestionOneQuizThree.add(Answer.builder().description("Barometer").isRight(false).question(questionOneQuizThree).build());

        questionOneQuizThree.setAnswers(answersQuestionOneQuizThree);

        questionsQuizThree.add(questionOneQuizThree);

        quizThree.setQuestions(questionsQuizThree);

        quizService.save(quizThree);

        Quiz quizFour = Quiz.builder().name("Philosophy quiz.").description("Some philosophy quiz...").specialty(philosophySpecialty).user(tutorUser).build();

        List<Question> questionsQuizFour = new ArrayList<>();

        Question questionOneQuizFour = Question.builder().description("The four main divisions of philosophy are metaphysics, epistemology, axiology, and ...?").quiz(quizFour).build();

        List<Answer> answersQuestionOneQuizFour = new ArrayList<>();

        answersQuestionOneQuizFour.add(Answer.builder().description("Bioethics").isRight(false).question(questionOneQuizFour).build());
        answersQuestionOneQuizFour.add(Answer.builder().description("Logic").isRight(true).question(questionOneQuizFour).build());
        answersQuestionOneQuizFour.add(Answer.builder().description("Aesthetics").isRight(false).question(questionOneQuizFour).build());
        answersQuestionOneQuizFour.add(Answer.builder().description("Categorical logic").isRight(false).question(questionOneQuizFour).build());

        questionOneQuizFour.setAnswers(answersQuestionOneQuizFour);

        questionsQuizFour.add(questionOneQuizFour);

        quizFour.setQuestions(questionsQuizFour);

        quizService.save(quizFour);

        Quiz quizFive = Quiz.builder().name("Computing quiz.").description("A trivial quiz on types of computers!").specialty(computingSpecialty).user(tutorUser).build();

        List<Question> questionsQuizFive = new ArrayList<>();

        Question questionOneQuizFive = Question.builder().description("The acronym PC stands for: ...?").quiz(quizFive).build();

        List<Answer> answersQuestionOneQuizFive = new ArrayList<>();

        answersQuestionOneQuizFive.add(Answer.builder().description("Private computer").isRight(false).question(questionOneQuizFive).build());
        answersQuestionOneQuizFive.add(Answer.builder().description("Personal computer").isRight(true).question(questionOneQuizFive).build());
        answersQuestionOneQuizFive.add(Answer.builder().description("Personal compact").isRight(false).question(questionOneQuizFive).build());
        answersQuestionOneQuizFive.add(Answer.builder().description("Public computer").isRight(false).question(questionOneQuizFive).build());

        questionOneQuizFive.setAnswers(answersQuestionOneQuizFive);

        questionsQuizFive.add(questionOneQuizFive);

        quizFive.setQuestions(questionsQuizFive);

        quizService.save(quizFive);

        Quiz quizSix = Quiz.builder().name("Astronomy quiz.").description("The great astronomy quiz...").specialty(astronomySpecialty).user(tutorUser).build();

        List<Question> questionsQuizSix = new ArrayList<>();

        Question questionOneQuizSix = Question.builder().description("Which galaxy is home to the Solar system?").quiz(quizSix).build();

        List<Answer> answersQuestionOneQuizSix = new ArrayList<>();

        answersQuestionOneQuizSix.add(Answer.builder().description("Milky Way").isRight(true).question(questionOneQuizSix).build());
        answersQuestionOneQuizSix.add(Answer.builder().description("Pluto").isRight(false).question(questionOneQuizSix).build());
        answersQuestionOneQuizSix.add(Answer.builder().description("Galileo Galilei").isRight(false).question(questionOneQuizSix).build());
        answersQuestionOneQuizSix.add(Answer.builder().description("Vega").isRight(false).question(questionOneQuizSix).build());

        questionOneQuizSix.setAnswers(answersQuestionOneQuizSix);

        questionsQuizSix.add(questionOneQuizSix);

        quizSix.setQuestions(questionsQuizSix);

        quizService.save(quizSix);

        log.info("Quizzes are loaded.");
    }
}
