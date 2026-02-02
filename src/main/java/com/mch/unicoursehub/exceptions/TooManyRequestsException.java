package com.mch.unicoursehub.exceptions;

/**
 * Exception thrown when a user exceeds the allowed number of requests
 * within a given time frame.
 *
 * <p>
 * This exception maps to HTTP status code 429 (TOO MANY REQUESTS).
 * It is typically used for rate-limiting mechanisms to prevent abuse
 * or overuse of a service.
 * </p>
 */
public class TooManyRequestsException extends RuntimeException {
}
