package com.gmail.artsiombaranau.thetutor.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answers")
public class Answer extends BaseEntity{

    private static final long serialVersionUID = 1L;

    public Answer(Long id, String description, Boolean isRight, Question question) {
        super(id);
        this.description = description;
        this.isRight = isRight;
        this.question = question;
    }

    @Column(name = "description")
    @NotEmpty
    @Size(min = 1, max = 255)
    private String description;

    @Column(name = "is_right", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isRight;

    @ManyToOne //change to another cascade type! (cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private Question question;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;

        Answer answer = (Answer) o;

        if (description != null ? !description.equals(answer.description) : answer.description != null) return false;
        if (isRight != null ? !isRight.equals(answer.isRight) : answer.isRight != null) return false;
        return question != null ? question.equals(answer.question) : answer.question == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (isRight != null ? isRight.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
