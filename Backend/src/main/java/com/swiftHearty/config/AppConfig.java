package com.swiftHearty.config;

import com.swiftHearty.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {

                return userRepository.findByPhoneNumber(phoneNumber)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }

        };
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }





}
