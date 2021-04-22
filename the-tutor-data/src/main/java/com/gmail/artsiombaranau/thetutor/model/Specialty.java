package com.gmail.artsiombaranau.thetutor.model;

import com.gmail.artsiombaranau.thetutor.enums.Specialties;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "specialties")
public class Specialty extends BaseEntity {

    @Builder
    public Specialty(Long id, Specialties description) {
        super(id);
        this.description = description;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "description", unique = true)
    private Specialties description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialty)) return false;

        Specialty specialty = (Specialty) o;

        return description == specialty.description;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
