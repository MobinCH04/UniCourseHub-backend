package com.mch.unicoursehub.exceptions;

import com.mch.unicoursehub.ConstErrors;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Custom exception thrown when a resource is not found.
 * This exception extends RuntimeException and allows for an optional message
 * to be provided to describe the reason for the exception.
 */
@ControllerAdvice
public class NotFoundException extends RuntimeException {

    private String message;
    private int errorCode;

    /**
     * Default constructor for the NotFoundException.
     * The message will be null by default.
     */
    public NotFoundException() {

    }

    /**
     * Constructor to create an instance of NotFoundException with a message.
     *
     * @param message the message describing why the resource was not found
     */
    public NotFoundException(String message) {
        this.message = message;
    }

    public NotFoundException(ConstErrors.Error error) {
        this.message = error.getMessage();
        this.errorCode = error.getErrorCode();
    }

    public NotFoundException(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * Gets the message associated with the exception.
     *
     * @return the message describing why the resource was not found
     */
    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
