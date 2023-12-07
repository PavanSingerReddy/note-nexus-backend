package com.pavansingerreddy.note.exception;

// This exception is thrown when the user provides invalid user details
public class InvalidUserDetailsException extends Exception {
    public InvalidUserDetailsException(String message){
        super(message);
    }
}
