package com.mch.unicoursehub.exceptions.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

/**
 * Represents a response for a bad request (HTTP 400) with detailed error information.
 * This class extends {@link DefaultResponse} and includes a list of errors along with a message and status code.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BadRequestResponse extends DefaultResponse {

    /**
     * The message that describes the response.
     */
    private String message;
    /**
     * The HTTP status code (always 400 for bad request).
     */
    private int code;
    private int errorCode;

    /**
     * A list of detailed errors associated with the bad request.
     */
    private List<Error> errors;

    /**
     * Constructs a new BadRequestResponse with the specified list of errors.
     *
     * @param errors the list of errors that caused the bad request
     */
    public BadRequestResponse(List<Error> errors) {
        this.message = "bad request";
        this.code = 400;
        this.errors = errors;
    }

    /**
     * Constructs a new BadRequestResponse with the specified message.
     *
     * @param message the message describing the bad request
     */
    public BadRequestResponse(String message) {
        this.message = message;
        this.code = 400;
    }

    public BadRequestResponse(String message, int errorCode) {
        this.message = message;
        this.code = 400;
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BadRequestResponse with the specified message and status code.
     *
     * @param message the message describing the bad request
     * @param code the HTTP status code to use
     */
    public BadRequestResponse(String message, int code, int errorCode) {
        this.message = message;
        this.code = code;
        this.errorCode = errorCode;
    }

    /**
     * Default constructor that sets the message to "bad request" and the code to 400.
     */
    public BadRequestResponse() {
        this.message = "bad request";
    }

    /**
     * Returns the message describing the bad request.
     *
     * @return the message describing the bad request
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the HTTP status code (400 for bad request).
     *
     * @return the HTTP status code
     */
    @Override
    public int getCode() {
        return code;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public List<Error> getErrors() {
        return errors;
    }
}

