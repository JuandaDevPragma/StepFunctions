package com.juanojedadev.pragma.editlambda.body.mapper;

import com.juanojedadev.pragma.editlambda.body.domain.User;
import com.juanojedadev.pragma.editlambda.body.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IGlobalMapper {

    IGlobalMapper INSTANCE = Mappers.getMapper(IGlobalMapper.class);
    User userEntityToUserDomain(UserEntity user);
}
