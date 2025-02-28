package com.juanojedadev.pragma.createlambda.body.domain.usecase;

import com.juanojedadev.pragma.createlambda.body.domain.model.User;

public interface IUserUseCase {
    User createUser(User userDto);
}
