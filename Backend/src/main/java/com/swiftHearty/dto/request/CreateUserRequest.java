package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be valid and contain 10 to 15 digits"
    )
    private String phoneNumber;


    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "^(TENANT|SECURITY|ADMIN)$",
            message = "Role must be either 'TENANT', 'SECURITY' or 'ADMIN'"
    )
    private String role;

}
