package com.roqia.Drive_demo.mapper;


import com.roqia.Drive_demo.dto.UserDto;
import com.roqia.Drive_demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper instance = Mappers.getMapper(UserMapper.class);
    public UserDto mapToDto(User user);
    public User mapTouser(UserDto dto);

}
