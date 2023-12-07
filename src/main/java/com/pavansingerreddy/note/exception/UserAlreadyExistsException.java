package com.pavansingerreddy.note.exception;


// while creating a new user if the user is already present in the database and is enabled also then we throw this exception
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
