package com.pavansingerreddy.note.exception;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
