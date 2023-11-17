package com.pavansingerreddy.note.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pavansingerreddy.note.exception.UserNotFoundException;

// Controller advice which handles the errors occured in all my Rest Api endpoints
@RestControllerAdvice
public class GlobalRESTExceptionHandler {

    // sending 400 as http response if we got MethodArgumentNotValidException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // The MethodArgumentNotValidException is an exception that is thrown by Spring
    // Boot when validation on an argument annotated with @Valid fails. This
    // typically happens during the deserialization process when a client sends a
    // request to a Spring REST endpoint
    @ExceptionHandler(MethodArgumentNotValidException.class)
    // This method handles the MethodArgumentNotValidException which is thrown when
    // validation fails on an argument annotated with @Valid.
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {

        // Create a new HashMap to store the field names and their corresponding error
        // messages.
        Map<String, String> errorMap = new HashMap<>();
        // Get the BindingResult from the exception. The BindingResult contains the
        // result of the validation.
        // Call getFieldErrors() to get a list of FieldError objects. Each FieldError
        // represents a validation error on a specific field.
        // Use a forEach loop to iterate over each FieldError.
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            // For each FieldError, get the field name with getField() and the error message
            // with getDefaultMessage().
            // Put the field name and error message into the errorMap.
            errorMap.put(error.getField(), error.getDefaultMessage());

        });
        // Return the errorMap. The keys in the map are the field names, and the values
        // are the corresponding error messages.
        return errorMap;

    }

    // sending 400 as http response if we got DataIntegrityViolationException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // The DataIntegrityViolationException is a runtime exception thrown by Spring
    // when an operation violates a database integrity constraint
    @ExceptionHandler(DataIntegrityViolationException.class)
    // function which handles the DataIntegrityViolationException
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMostSpecificCause().getMessage());

        return errorMap;
    }

    // sending 400 as http response if we got IllegalArgumentException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // The IllegalArgumentException is an unchecked exception in Java that is thrown
    // to indicate that a method has been passed an illegal or inappropriate
    // argument
    @ExceptionHandler(IllegalArgumentException.class)
    // function which handles the IllegalArgumentException
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException exception) {

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", "please provide a valid user details");

        return errorMap;

    }

    // sending 400 as http response if we got UserNotFoundException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // This is a custom exception which get's thrown if the user is not found
    @ExceptionHandler(UserNotFoundException.class)
    // function which handles the UserNotFoundException
    public Map<String, String> handleUserNotFoundException(UserNotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    // sending 400 as http response if we got HttpMediaTypeNotSupportedException
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // The HttpMediaTypeNotSupportedException is an exception thrown by Spring when
    // a client sends a request with a content type that is not supported by the
    // request handler. This typically happens when a client POSTs, PUTs, or PATCHes
    // content of a type not supported by the request handler
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    // function which handles the HttpMediaTypeNotSupportedException
    public Map<String, String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

    // sending 403 as http response if we got AccessDeniedException
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    // In the context of Spring Security, an AccessDeniedException
    // is thrown when an authenticated user tries to access a resource for which
    // they do not have the necessary authority. This typically happens when a user
    // tries to access a protected endpoint or resource but does not have the
    // required roles or permissions
    @ExceptionHandler(AccessDeniedException.class)
    // function which handles the AccessDeniedException
    public Map<String, String> handleHttpMediaTypeNotSupportedException(AccessDeniedException exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", "You don't have the required permissions to access the resource !!!");

        return errorMap;
    }

    // sending 400 as http response if we got any other Exception
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    // Exception is the super class of all exceptions so if any other exceptions
    // occur this handler will work
    @ExceptionHandler(Exception.class)
    // function which handles the Exception
    public Map<String, String> handleAllException(Exception exception) {
        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("errorMessage", exception.getMessage());

        return errorMap;
    }

}
