package com.gmail.artsiombaranau.thetutor.utils;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.model.Question;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuizUtils {

    public Quiz createEmptyQuiz(User user) {
        if (user != null) {
            Quiz quiz = Quiz.builder().user(user).build();

            Question questionOne = Question.builder().quiz(quiz).build();
            List<Answer> answersQuestionOne = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                answersQuestionOne.add(Answer.builder().isRight(Boolean.FALSE).question(questionOne).build());
            }
            questionOne.setAnswers(answersQuestionOne);

            Question questionTwo = Question.builder().quiz(quiz).build();
            List<Answer> answersQuestionTwo = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                answersQuestionTwo.add(Answer.builder().isRight(Boolean.FALSE).question(questionTwo).build());
            }
            questionTwo.setAnswers(answersQuestionTwo);

//        Question questionThree = Question.builder().build();
//        List<Answer> answersQuestionThree = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionThree.add(Answer.builder().isRight(Boolean.FALSE).question(questionThree).build());
//        }
//        questionThree.setAnswers(answersQuestionThree);
//
//        Question questionFour = Question.builder().build();
//        List<Answer> answersQuestionFour = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionFour.add(Answer.builder().isRight(Boolean.FALSE).question(questionFour).build());
//        }
//        questionFour.setAnswers(answersQuestionFour);
//
//        Question questionFive = Question.builder().build();
//        List<Answer> answersQuestionFive = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionFive.add(Answer.builder().isRight(Boolean.FALSE).question(questionFive).build());
//        }
//        questionFive.setAnswers(answersQuestionFive);
//
//        Question questionSix = Question.builder().build();
//        List<Answer> answersQuestionSix = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionSix.add(Answer.builder().isRight(Boolean.FALSE).question(questionSix).build());
//        }
//        questionSix.setAnswers(answersQuestionSix);
//
//        Question questionSeven = Question.builder().build();
//        List<Answer> answersQuestionSeven = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            answersQuestionSeven.add(Answer.builder().isRight(Boolean.FALSE).question(questionSeven).build());
//        }
//        questionSeven.setAnswers(answersQuestionSeven);

//        List<Question> questions = new ArrayList<>(List.of(questionOne, questionTwo, questionThree, questionFour, questionFive, questionSix, questionSeven));
            List<Question> questions = new ArrayList<>(List.of(questionOne, questionTwo));

            quiz.setQuestions(questions);

            return quiz;
        }
        return null;
    }
}
