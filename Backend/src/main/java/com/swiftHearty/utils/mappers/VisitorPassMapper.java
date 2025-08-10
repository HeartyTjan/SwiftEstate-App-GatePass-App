package com.swiftHearty.utils.mappers;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.dto.response.VisitorPassResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitorPassMapper {

    @Mapping(target = "timeIn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "open", constant = "true")
    @Mapping(target = "securityName", source = "securityName")
    @Mapping(target = "timeOut", ignore = true)
    @Mapping(target = "accessCode", source = "accessCode")
    VisitorPass mapToPass(AccessCode accessCode, String securityName);

    @Mapping(target = "visitorPass", source = "visitorPass")
    @Mapping(target = "success", constant = "true")
    VisitorPassResponse mapToResponse(VisitorPass visitorPass);
}
