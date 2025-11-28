package com.mch.unicoursehub.exceptions;

/**
 * Exception thrown when access is denied to a resource.
 * This exception is typically used when a user does not have the required permissions to access a specific resource.
 */
public class AccessDeniedException extends RuntimeException{
    private String message;

    /**
     * Constructor to create an instance of AccessDeniedException with a message and cause.
     *
     * @param message the message describing the access denial
     * @param cause the cause of the exception
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * Constructor to create an instance of AccessDeniedException with a message.
     *
     * @param message the message describing the access denial
     */
    public AccessDeniedException(String message) {
        super(message);
        this.message = message;
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

    /**
     * Sets the message for the exception.
     *
     * @param message the message to be set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

