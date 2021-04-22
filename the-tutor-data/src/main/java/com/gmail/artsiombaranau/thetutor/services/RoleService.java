package com.gmail.artsiombaranau.thetutor.services;

import com.gmail.artsiombaranau.thetutor.enums.Roles;
import com.gmail.artsiombaranau.thetutor.model.Role;

public interface RoleService extends CrudService<Role, Long> {
    Role findByName (Roles name);
}
