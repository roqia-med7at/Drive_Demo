package com.roqia.Drive_demo.mapper;


import com.roqia.Drive_demo.dto.TokenDto;
import com.roqia.Drive_demo.security.jwt.model.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenMapper instance = Mappers.getMapper(TokenMapper.class);
    public TokenDto mapToDto(RefreshToken token);
    public RefreshToken mapToToken(TokenDto dto);
}
