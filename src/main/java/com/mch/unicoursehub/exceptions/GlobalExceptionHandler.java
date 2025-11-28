package com.mch.unicoursehub.exceptions;

import com.mch.unicoursehub.exceptions.responses.BadRequestResponse;
import com.mch.unicoursehub.exceptions.responses.Error;
import com.mch.unicoursehub.exceptions.responses.ForbiddenResponse;
import com.mch.unicoursehub.exceptions.responses.NotFoundResponse;
import com.mch.unicoursehub.utils.HttpRequestData;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler that intercepts various exceptions thrown in the application.
 * It provides a centralized mechanism for handling exceptions and generating appropriate responses.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation exceptions (e.g., MethodArgumentNotValidException).
     *
     * @param ex the exception to handle
     * @return a response entity with the validation errors
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("validation exception -> " + ex.getMessage(), ex.getCause());

        List<Error> errorList = ex.getBindingResult().getAllErrors()
                .stream().map(item ->
                        Error.builder()
                                .fieldName(((FieldError) item).getField())
                                .errorMessage(item.getDefaultMessage())
                                .build()).toList();

        return new ResponseEntity<>(new BadRequestResponse(errorList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TooManyRequestsException.class})
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleTooManyRequestException(TooManyRequestsException ex, HttpServletResponse response) {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    /**
     * Handles not found exceptions (e.g., NotFoundException, NullPointerException).
     *
     * @param ex the exception to handle
     * @return a response entity with the error message
     */
    @ExceptionHandler({NotFoundException.class, NullPointerException.class})
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        log.error("not found exception -> " + ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(new NotFoundResponse(ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ExistException.class})
    public ResponseEntity<?> handleExistException(ExistException ex) {
        log.error("exist exception -> " + ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage(), ex.getErrorCode()), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles bad request exceptions (e.g., BadRequestException).
     *
     * @param ex the exception to handle
     * @return a response entity with the error message
     */
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        log.error("Bad request exception -> " + ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage(), ex.getErrorCode()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation exceptions (e.g., ConstraintViolationException, ValidationException).
     *
     * @param ex the exception to handle
     * @return a response entity with the validation error message
     */
    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class})
    public ResponseEntity<?> handleValidationException(Exception ex) {
        log.error("validation exception -> " + ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles access denied exceptions (e.g., BadCredentialsException, AuthenticationException).
     *
     * @param ex the exception to handle
     * @return a response entity with a forbidden access response
     */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<?> handleAccessDenied(RuntimeException ex) {
        log.error("forbidden access exception -> " + ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getSuppressed(), ex.getStackTrace());
        return new ResponseEntity<>(new ForbiddenResponse(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles expired JWT exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with a forbidden access response
     */
    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error("Expired JWT Exception -> " + ex.getMessage());
        return new ResponseEntity<>(new ForbiddenResponse(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles access denied exceptions (e.g., AccessDeniedException).
     *
     * @param ex the exception to handle
     * @return a response entity with an unauthorized error response
     */
    @ExceptionHandler({AccessDeniedException.class, com.mch.unicoursehub.exceptions.AccessDeniedException.class})
    public ResponseEntity<?> handleAccessDenied(Exception ex) {
        log.error("access denied -> " + ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getSuppressed(), ex.getStackTrace());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles JWT-related exceptions (e.g., JwtException, SignatureException).
     *
     * @param ex the exception to handle
     * @return a response entity with an unauthorized error response
     */
    @ExceptionHandler({JwtException.class, SignatureException.class, java.security.SignatureException.class})
    public ResponseEntity<?> handleJWTException(Exception ex) {
        log.error("jwt exception  -> " + ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getSuppressed(), ex.getStackTrace());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage(), 401), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles optimistic locking failure exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with a conflict error message
     */
    @ExceptionHandler({OptimisticLockingFailureException.class})
    public ResponseEntity<?> handleJWTException(OptimisticLockingFailureException ex) {
        log.error("validation exception -> " + ex.getMessage(), ex.getCause());
        return new ResponseEntity<>(new BadRequestResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    /**
     * Handles the case where no handler is found for the request.
     *
     * @param ex the exception to handle
     * @return a response entity with a not implemented response
     */
    @ExceptionHandler({NoHandlerFoundException.class})
    public ResponseEntity<?> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        log.error("no handler found for this request -> " + ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getSuppressed(), ex.getStackTrace());

        HttpRequestData requestData = new HttpRequestData();

        log.info("request info: " + requestData.getData());
        return new ResponseEntity<>(new BadRequestResponse("Not Implemented", 501), HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Handles all other unsupported exceptions.
     *
     * @param ex the exception to handle
     * @return a response entity with a bad request response
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> unsupportedException(Exception ex) {

        log.error("unsupported exception happened... " + ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getSuppressed(), ex.getStackTrace());
        HttpRequestData requestData = new HttpRequestData();

        log.info("request info: " + requestData.getData());
        return new ResponseEntity<>(new BadRequestResponse("bad request exception", 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getReason());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

}

