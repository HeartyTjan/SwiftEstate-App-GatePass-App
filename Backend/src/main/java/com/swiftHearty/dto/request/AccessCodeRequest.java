package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccessCodeRequest {
    @NotBlank(message = "User id is required")
    @NotNull(message = "User id must not be null")
    private  String userId;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be valid and contain 10 to 15 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Visitor's name is required")
    private String visitorName;

    @NotBlank(message = "Apartment Id is required")
    private String apartmentId;

}
