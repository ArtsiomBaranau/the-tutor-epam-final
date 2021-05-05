package com.gmail.artsiombaranau.thetutor.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder
    public Question(Long id, String description, List<Answer> answers, Quiz quiz) {
        super(id);
        this.description = description;
        this.answers = answers;
        this.quiz = quiz;
    }

    @Column(name = "description", nullable = false)
    @NotEmpty
    @Size(min = 1, max = 255)
    private String description;

    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "question")
    @Valid
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;

        Question question = (Question) o;

        if (description != null ? !description.equals(question.description) : question.description != null)
            return false;
        return quiz != null ? quiz.equals(question.quiz) : question.quiz == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (quiz != null ? quiz.hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}