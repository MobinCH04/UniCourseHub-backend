package com.mch.unicoursehub.exceptions;

import com.mch.unicoursehub.ConstErrors;


/**
 * Exception thrown when a bad request is made, typically due to invalid input or parameters.
 */
public class BadRequestException extends RuntimeException {

    private String message;
    private int errorCode;

    /**
     * Constructor to create an instance of BadRequestException with a message and cause.
     *
     * @param message the message describing the bad request
     * @param cause the cause of the exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * Constructor to create an instance of BadRequestException with a message.
     *
     * @param message the message describing the bad request
     */
    public BadRequestException(String message) {
        super(message);
        this.message = message;
    }

    public BadRequestException(ConstErrors.Error error) {
        super(error.getMessage());
        this.message = error.getMessage();
        this.errorCode = error.getErrorCode();
    }

    public BadRequestException(String message, int errorCode) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * Gets the message of the exception.
     *
     * @return the exception message
     */
    @Override
    public String getMessage() {
        return message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the message for the exception.
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

