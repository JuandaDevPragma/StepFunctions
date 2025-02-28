package com.juanojedadev.pragma.createlambda.body.domain.ports;

import com.juanojedadev.pragma.createlambda.body.domain.model.User;

public interface IPersistencePort {
    public User createUser(User user);
}
