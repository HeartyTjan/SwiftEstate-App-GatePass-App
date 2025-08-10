package com.swiftHearty.utils.mappers;

import com.swiftHearty.data.model.SecurityProfile;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.dto.request.UpdateSecurityProfileRequest;
import com.swiftHearty.dto.request.UpdateTenantProfileRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {

    void updateTenantProfileFromDto(UpdateTenantProfileRequest dto, @MappingTarget TenantProfile entity);
    void updateSecurityProfileFromDto(UpdateSecurityProfileRequest request, @MappingTarget SecurityProfile entity);
}
