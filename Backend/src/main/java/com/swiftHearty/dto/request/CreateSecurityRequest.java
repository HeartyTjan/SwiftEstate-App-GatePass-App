package com.swiftHearty.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class CreateSecurityRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be valid and contain 10 to 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;
    @NotBlank
    private String role;

}
