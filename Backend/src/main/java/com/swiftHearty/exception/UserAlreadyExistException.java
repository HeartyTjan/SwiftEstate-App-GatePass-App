package com.swiftHearty.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String message){
        super(message);
    }
}
