package com.swiftHearty.data.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "security_profiles")
public class SecurityProfile extends Profile {

    @Id
    private String id;

//    @NotBlank
//    private String userId;
//
//    @NotBlank(message = "First name is required")
//    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
//    private String lastName;

    @NotBlank
    private String gateAssigned;

    private String staffId;

    @NotBlank
    private String shiftTime;

}

