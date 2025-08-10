package com.swiftHearty.services;

public interface SmsService {
    String sendSms(String phoneNumber, String message);
}

