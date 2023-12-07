package com.pavansingerreddy.note.exception;

// This exception is thrown when user's passwords does not match
public class PasswordDoesNotMatchException extends Exception {
    public PasswordDoesNotMatchException(String message){
        super(message);
    }
}
