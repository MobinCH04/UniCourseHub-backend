package com.mch.unicoursehub.exceptions.responses;

/**
 * Abstract class representing a response with a message and code.
 * It serves as a base class for all response types that include a message and a status code.
 */
public abstract class DefaultResponse extends Response {
    /**
     * Gets the message associated with the response.
     *
     * @return the message of the response
     */
    public abstract String getMessage();

    /**
     * Gets the HTTP status code for the response.
     *
     * @return the HTTP status code
     */
    public abstract int getCode();
}

