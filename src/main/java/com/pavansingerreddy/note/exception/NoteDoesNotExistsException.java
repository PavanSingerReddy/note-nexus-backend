package com.pavansingerreddy.note.exception;

// This exception is thrown if the note does not exists for a given user
public class NoteDoesNotExistsException extends Exception {
    public NoteDoesNotExistsException(String message){
        super(message);
    }
}
