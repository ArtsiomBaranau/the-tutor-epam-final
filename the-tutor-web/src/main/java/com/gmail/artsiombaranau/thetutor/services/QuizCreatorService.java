package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.model.Answer;
import com.gmail.artsiombaranau.thetutor.model.Question;
import com.gmail.artsiombaranau.thetutor.model.Quiz;
import com.gmail.artsiombaranau.thetutor.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizCreatorService {

    public Quiz createEmptyQuiz(User user, int questionsQuantity) {
        if (user != null) {
            Quiz quiz = Quiz.builder().user(user).build();

            List<Question> questions = new ArrayList<>();

            for (int j = 0; j < questionsQuantity; j++) {
                Question question = Question.builder().quiz(quiz).build();

                List<Answer> answers = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    answers.add(Answer.builder().isRight(Boolean.FALSE).question(question).build());
                }

                question.setAnswers(answers);

                questions.add(question);
            }
            quiz.setQuestions(questions);

            return quiz;
        }

        return null;
    }
}
