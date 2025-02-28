package com.juanojedadev.pragma.createlambda.body.mapper;

import com.juanojedadev.pragma.createlambda.body.domain.model.User;
import com.juanojedadev.pragma.createlambda.body.dto.UserDto;
import com.juanojedadev.pragma.createlambda.body.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IGlobalMapper {

    IGlobalMapper INSTANCE = Mappers.getMapper(IGlobalMapper.class);

    User userDtoToUserDomain(UserDto userDto);
    User userEntityToUserDomain(UserEntity user);
    UserEntity userDomainToUserEntity(User user);
}
