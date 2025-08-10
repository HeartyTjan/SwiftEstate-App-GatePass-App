package com.swiftHearty.services.impl;

import com.swiftHearty.services.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TermiiSmsService implements SmsService {
    @Value("${termii.api_key}")
    private String apiKey;

    @Value("${termii.sender_id}")
    private String senderId;

    @Value("${termii.sms_url}")
    private String smsUrl;

    private final RestTemplate restTemplate;


    @Override
    public String sendSms(String phoneNumber, String message) {
        Map<String, Object> body = getBody(phoneNumber, message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(smsUrl, request, String.class);
        return phoneNumber;
    }

    private Map<String, Object> getBody(String phoneNumber, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("api_key", apiKey);
        body.put("message_type", "NUMERIC");
        body.put("to", phoneNumber);
        body.put("from", senderId);
        body.put("channel", "generic");
        body.put("pin_attempts", 3);
        body.put("pin_time_to_live", 5);
        body.put("pin_length", 6);
        body.put("pin_placeholder", "< 1234 >");
        body.put("message_text", message);
        body.put("pin_type", "NUMERIC");
        return body;
    }


}
