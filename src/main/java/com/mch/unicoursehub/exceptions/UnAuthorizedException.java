package com.mch.unicoursehub.exceptions;

/**
 * Exception thrown when a user attempts an action without proper authorization.
 *
 * <p>
 * This exception maps to HTTP status code 401 (UNAUTHORIZED). It is
 * typically used when a user tries to access a protected resource
 * or perform an action they are not allowed to.
 * </p>
 */
public class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(String message) {
        super(message);
    }
}
