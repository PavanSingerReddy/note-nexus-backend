package com.pavansingerreddy.note.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pavansingerreddy.note.exception.InvalidUserDetailsException;
import com.pavansingerreddy.note.exception.PasswordDoesNotMatchException;
import com.pavansingerreddy.note.exception.UserNotFoundException;

@RestControllerAdvice
public class GlobalRESTExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {

        Map<String, String> errorMap = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {

            errorMap.put(error.getField(), error.getDefaultMessage());

        });

        return errorMap;

    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(DataIntegrityViolationException.class)

    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage",
                "Sorry, we couldn't process your request due to a data integrity issue. Please ensure your input is valid and try again.");

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(IllegalArgumentException.class)

    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException exception) {

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", "please provide a valid user details");

        return errorMap;

    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(UserNotFoundException.class)

    public Map<String, String> handleUserNotFoundException(UserNotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)

    public Map<String, String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage",
                "Sorry, we couldn't process your request because the provided media type is not supported. Please ensure your request has the correct 'Content-Type' header and try again.");

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)

    @ExceptionHandler(AccessDeniedException.class)

    public Map<String, String> handleAccessDeniedException(AccessDeniedException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", "You don't have the required permissions to access the resource !!!");

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(InvalidUserDetailsException.class)

    public Map<String, String> handleInvalidUserDetailsException(InvalidUserDetailsException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(PasswordDoesNotMatchException.class)

    public Map<String, String> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(DisabledException.class)

    public Map<String, String> handleDisabledException(DisabledException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)

    @ExceptionHandler(Exception.class)

    public Map<String, String> handleAllException(Exception exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", "Some unknown error occurred please try again");

        return errorMap;
    }

}
