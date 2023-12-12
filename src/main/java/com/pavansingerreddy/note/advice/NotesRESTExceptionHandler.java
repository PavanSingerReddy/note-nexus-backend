package com.pavansingerreddy.note.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pavansingerreddy.note.controller.NotesController;
import com.pavansingerreddy.note.exception.NoteDoesNotExistsException;

@RestControllerAdvice(basePackageClasses = NotesController.class)
public class NotesRESTExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(NoteDoesNotExistsException.class)

    public Map<String, String> handleAllException(NoteDoesNotExistsException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("errorMessage", exception.getMessage());
        return errorMap;
    }

}
