package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.model.User;
import org.springframework.data.domain.Page;

public interface UserService extends CrudService<User, Long> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findPaginated(int pageNumber, int pageSize);

}
