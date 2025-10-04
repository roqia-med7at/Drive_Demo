package com.roqia.Drive_demo.mapper;

import com.roqia.Drive_demo.dto.response.SharedItemResponse;
import com.roqia.Drive_demo.model.SharedItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SharedItemMapper {
    SharedItemMapper mapper = Mappers.getMapper(SharedItemMapper.class);
    @Mappings({
            @Mapping(expression = "java(sharedItem.getSharedItem().getName())",target = "itemName"),
            @Mapping(expression ="java(sharedItem.getSharedItem().getId())" ,target = "itemId"),
            @Mapping(expression = "java(sharedItem.getOwnedBy().getEmail())",target = "ownedBy")
    })
    SharedItemResponse mapToDto (SharedItem sharedItem);
}
