package com.swiftHearty.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class UpdateTenantProfileRequest {
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Apartment Number is required")
    private String apartmentNumber;

    private String state;

    private String country;

    private String estateName;
}
