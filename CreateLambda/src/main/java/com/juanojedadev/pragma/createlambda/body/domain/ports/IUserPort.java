package com.juanojedadev.pragma.createlambda.body.domain.ports;

import com.juanojedadev.pragma.createlambda.body.domain.model.User;

public interface IUserPort {
    User createUser(User userDto);
}
