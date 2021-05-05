package com.gmail.artsiombaranau.thetutor.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Builder
    public User(Long id, String username, String email, String password, String encryptedPassword, List<Role> roles, String firstName, String lastName) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Column(name = "username", nullable = false, unique = true)
    @NotEmpty
    @Size(min = 3, max = 255)
    @Pattern(regexp = "[A-Za-z0-9_-]+")
    private String username;

    @Column(name = "email", unique = true)
    @Email
    private String email;

    @Transient
    @Size(min = 3)
    private String password;

    @Column(name = "encrypted_password", nullable = false)
    private String encryptedPassword;

    @Singular
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @Column(name = "first_name")
    @NotEmpty
    @Size(min = 2, max = 255)
    @Pattern(regexp = "[A-Za-z]+")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    @Size(min = 2, max = 255)
    @Pattern(regexp = "[A-Za-z]+")
    private String lastName;

    public void addRole(Role role) {
        if (role != null)
            this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (role != null)
            this.roles.remove(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (encryptedPassword != null ? !encryptedPassword.equals(user.encryptedPassword) : user.encryptedPassword != null)
            return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        return lastName != null ? lastName.equals(user.lastName) : user.lastName == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (encryptedPassword != null ? encryptedPassword.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }
}
