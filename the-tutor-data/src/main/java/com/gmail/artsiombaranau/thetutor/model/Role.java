package com.gmail.artsiombaranau.thetutor.model;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Builder
    public Role(Long id, Roles name) {
        super(id);
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true)
    private Roles name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        return name == role.name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
