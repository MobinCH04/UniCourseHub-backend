package com.mch.unicoursehub.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a conflict occurs in the application.
 *
 * <p>
 * This exception maps to HTTP status code 409 (CONFLICT). It is
 * typically used when a requested operation cannot be performed
 * because it would result in a duplicate resource or violate
 * business rules.
 * </p>
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /**
     * Constructs a new ConflictException with the specified message.
     *
     * @param msg the detail message explaining the conflict
     */
    public ConflictException(String msg) { super(msg); }
}
