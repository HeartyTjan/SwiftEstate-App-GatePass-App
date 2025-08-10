package com.swiftHearty.dto.response;

import com.swiftHearty.data.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginResponse {
    private String message;
    private String token;
    private User user;
    private boolean isSuccess;
}
