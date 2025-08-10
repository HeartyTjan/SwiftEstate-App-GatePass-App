package com.swiftHearty.utils.mappers;

import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.dto.response.AccessCodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessCodeMapper {

    @Mapping(target = "accessCode", source = "accessCode")
//    @Mapping(target = "visitorPass.accessCode", ignore = true)
    @Mapping(target = "success", constant = "true")
    AccessCodeResponse mapToResponse(String accessCode, VisitorPass visitorPass);


}

