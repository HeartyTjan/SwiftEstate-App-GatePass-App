package com.swiftHearty.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class CreateNewUserResponse {
    private String Id;
    private String phoneNumber;
    private String role;

    private Object profile;
}
