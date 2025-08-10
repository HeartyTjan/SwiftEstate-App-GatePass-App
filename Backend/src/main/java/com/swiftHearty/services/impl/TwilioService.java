package com.swiftHearty.services.impl;

import com.swiftHearty.services.SmsService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TwilioService implements SmsService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Value("${twilio.phone.number}")
    private String fromPhone;

    @Override
    public String sendSms(String phoneNumber, String messageBody) {
        try {
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+" + phoneNumber;
            }

            Message message = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromPhone),
                    messageBody
            ).create();

            logger.info("Sent SMS with SID: {}", message.getSid());
            logger.info("Sending sms from twilio : {}", fromPhone);
            return message.getSid();
        } catch (Exception e) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return null;
        }
    }
}
