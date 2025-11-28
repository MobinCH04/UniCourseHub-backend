package com.mch.unicoursehub.exceptions;

import com.mch.unicoursehub.ConstErrors;

/**
 * Exception thrown when an entity already exists.
 * Typically used when trying to create a resource that already exists.
 */
public class ExistException extends RuntimeException {

    private Error error;
    private int errorCode;

    /**
     * Constructor to create an instance of ExistException with a message.
     *
     * @param message the message describing the exception
     */
    public ExistException(String message) {
        super(message);
    }

    public ExistException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ExistException(ConstErrors.Error error) {
        super(error.getMessage());
        this.errorCode = error.getErrorCode();
    }

    /**
     * Constructor to create an instance of ExistException with a message and an error.
     *
     * @param message the message describing the exception
     * @param error the error associated with the exception
     */
    private ExistException(String message, Error error) {
        super(message);
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

