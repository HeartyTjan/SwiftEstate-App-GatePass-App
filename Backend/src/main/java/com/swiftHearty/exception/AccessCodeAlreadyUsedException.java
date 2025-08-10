package com.swiftHearty.exception;

public class AccessCodeAlreadyUsedException extends RuntimeException {
    public AccessCodeAlreadyUsedException(String message) {
        super(message);
    }
}
