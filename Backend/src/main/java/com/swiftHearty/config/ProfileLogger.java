package com.swiftHearty.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfileLogger {

    @Autowired
    private Environment env;

    @PostConstruct
    public void logActiveProfiles() {
        System.out.println("Active profiles: " + Arrays.toString(env.getActiveProfiles()));
    }
}
