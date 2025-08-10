package com.swiftHearty.utils.mappers;

import com.swiftHearty.data.model.WhiteList;
import com.swiftHearty.dto.request.AddWhiteListRequest;
import com.swiftHearty.dto.response.WhiteListResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface WhiteListMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    WhiteList requestToWhiteList(AddWhiteListRequest request);
    WhiteListResponse whiteListToResponse(WhiteList whiteList);

    @AfterMapping
    default void setCreatedAt(@MappingTarget WhiteList whiteList) {
        if (whiteList.getCreatedAt() == null) {
            whiteList.setCreatedAt(LocalDateTime.now());
        }
    }
}
