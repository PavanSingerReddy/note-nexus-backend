package com.pavansingerreddy.note.exception;


// if the user is not found for any operation then we throw this exception
public class UserNotFoundException extends Exception {
    
    public UserNotFoundException() {
    }
    public UserNotFoundException(String message) {
        super(message);
    }

}
