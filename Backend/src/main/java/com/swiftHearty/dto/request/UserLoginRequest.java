package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {


    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be valid and contain 10 to 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;
}
