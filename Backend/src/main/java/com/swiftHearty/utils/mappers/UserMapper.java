package com.swiftHearty.utils.mappers;

import com.swiftHearty.data.model.User;
import com.swiftHearty.dto.request.CreateSecurityRequest;
import com.swiftHearty.dto.request.CreateUserRequest;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.UserLoginResponse;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface UserMapper {

    CreateNewUserResponse userToResponse(User user);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    User requestToUser(CreateUserRequest request, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(randomPassword))")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    User requestToUser(CreateSecurityRequest request, @Context PasswordEncoder passwordEncoder, @Context String randomPassword);

    @Mapping(target = "success", constant = "true")
    UserLoginResponse mapToLoginResponse(String token, User user, String message);
}
