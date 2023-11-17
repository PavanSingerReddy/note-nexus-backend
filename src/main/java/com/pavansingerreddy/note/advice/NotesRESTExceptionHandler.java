package com.pavansingerreddy.note.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pavansingerreddy.note.controller.NotesController;
import com.pavansingerreddy.note.exception.NoteDoesNotExistsException;


// Controller advice which handles the errors occured in my NotesController endpoints
@RestControllerAdvice(basePackageClasses = NotesController.class)
public class NotesRESTExceptionHandler {

    // sending 400 as http response if we got NoteDoesNotExistsException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // custom exception which get's thrown if the notes does not exists
    @ExceptionHandler(NoteDoesNotExistsException.class)
    // function which handles the NoteDoesNotExistsException
    public Map<String, String> handleAllException(NoteDoesNotExistsException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", exception.getMessage());
        return errorMap;
    }

}
