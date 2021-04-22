package com.gmail.artsiombaranau.thetutor.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public User(Long id, String username, String password, String encryptedPassword, Set<Role> roles, String firstName, String lastName) {
        super(id);
        this.username = username;
        this.password = password;
        this.encryptedPassword = encryptedPassword;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Column(name = "username", unique = true)
    @NotNull
    private String username;

    @Transient
    private String password;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

//    @Singular
//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER) //do i need cascade type merge?
//    @JoinTable(name = "user_specialties",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
//    private Set<Specialty> specialties = new HashSet<>();

    @Singular
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE) //do i need cascade type merge?
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(name = "first_name")
    @NotNull
    private String firstName;

    @Column(name = "last_name")
    @NotNull
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
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (encryptedPassword != null ? !encryptedPassword.equals(user.encryptedPassword) : user.encryptedPassword != null)
            return false;
        if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
        return lastName != null ? lastName.equals(user.lastName) : user.lastName == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (encryptedPassword != null ? encryptedPassword.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }
}
