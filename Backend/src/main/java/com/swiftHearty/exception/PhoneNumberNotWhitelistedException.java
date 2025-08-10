package com.swiftHearty.exception;

public class PhoneNumberNotWhitelistedException extends RuntimeException {
    public PhoneNumberNotWhitelistedException(String message) {
        super(message);
    }
}
