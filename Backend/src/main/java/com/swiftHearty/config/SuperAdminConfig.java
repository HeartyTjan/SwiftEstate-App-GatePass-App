package com.swiftHearty.config;

import com.swiftHearty.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SuperAdminConfig {
    private final UserService userService;

    @Value("${super.admin_phone}")
    String phoneNumber;

    @Value("${super.admin_password}")
    String password;


    @PostConstruct
    public void initSuperAdmin() {
        userService.createSuperAdminIfNonAvailable(phoneNumber, password);
    }
}
