package com.gmail.artsiombaranau.thetutor.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizzes")
public class Quiz extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder
    public Quiz(Long id, String name, String description, Specialty specialty, List<Question> questions, User user) {
        super(id);
        this.name = name;
        this.description = description;
        this.specialty = specialty;
        this.questions = questions;
        this.user = user;
    }

    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @Column(name = "description")
    @NotNull
    private String description;

    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    @Singular
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "quiz")
    private List<Question> questions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz)) return false;

        Quiz quiz = (Quiz) o;

        if (name != null ? !name.equals(quiz.name) : quiz.name != null) return false;
        if (description != null ? !description.equals(quiz.description) : quiz.description != null) return false;
        if (specialty != null ? !specialty.equals(quiz.specialty) : quiz.specialty != null) return false;
        return user != null ? user.equals(quiz.user) : quiz.user == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (specialty != null ? specialty.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
