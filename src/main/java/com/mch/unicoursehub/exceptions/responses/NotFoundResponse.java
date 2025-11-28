package com.mch.unicoursehub.exceptions.responses;

/**
 * Represents a response indicating that a resource was not found (HTTP 404).
 * This response is returned when the requested resource does not exist.
 */
public class NotFoundResponse extends DefaultResponse {

    private String message;
    private int errorCode;

    /**
     * Default constructor.
     */
    public NotFoundResponse() {
    }

    /**
     * Constructor with a custom message.
     *
     * @param message the message describing the not found response
     */
    public NotFoundResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * Gets the message associated with the not found response.
     *
     * @return the message of the not found response
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets the HTTP status code for the response (404).
     *
     * @return the HTTP status code
     */
    @Override
    public int getCode() {
        return 404;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

