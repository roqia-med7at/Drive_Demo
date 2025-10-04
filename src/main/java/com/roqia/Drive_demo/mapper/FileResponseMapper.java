package com.roqia.Drive_demo.mapper;

import com.roqia.Drive_demo.dto.response.FileResponse;
import com.roqia.Drive_demo.model.File;
import com.roqia.Drive_demo.service.ItemService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",uses = ItemService.class)
public interface FileResponseMapper {
    FileResponseMapper fileResponseMapper = Mappers.getMapper(FileResponseMapper.class);

    @Mappings({@Mapping(expression = "java(file.getFolder().getId())", target = "folder_id"),
               @Mapping(expression = "java(file.getId())", target = "file_id"),
               @Mapping(target = "file_path",expression = "java(itemService.get_item_path(file))"),
               @Mapping(expression = "java(file.getName()+\".\"+file.getFileExtension())",target = "file")})
    FileResponse mapToDto(File file,@Context ItemService itemService);

    List<FileResponse> mapToDtos(List<File>files,@Context ItemService itemService);
}
