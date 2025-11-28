package com.mch.unicoursehub.exceptions.responses;

/**
 * Represents a response indicating access denial (HTTP 403).
 * This response type is returned when a user does not have permission to access a resource.
 */
public class ForbiddenResponse extends DefaultResponse {
    /**
     * Gets the message indicating that access is denied.
     *
     * @return the access denied message
     */
    @Override
    public String getMessage() {
        return "Access Denied";
    }

    /**
     * Gets the HTTP status code for the response (403).
     *
     * @return the HTTP status code
     */
    @Override
    public int getCode() {
        return 403;
    }
}

