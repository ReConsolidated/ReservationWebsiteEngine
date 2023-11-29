package io.github.reconsolidated.zpibackend.infrastracture.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {
    private boolean debug = true;

    @ExceptionHandler(value
            = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        StringBuilder bodyOfResponse = new StringBuilder("Illegal argument: %s".formatted(ex.getMessage()));
        if (debug) {
            for (StackTraceElement line : ex.getStackTrace()) {
                bodyOfResponse.append("\n");
                bodyOfResponse.append(line.toString());
            }
        }
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value
            = { NoSuchElementException.class })
    protected ResponseEntity<Object> handleIllegalArgument(
            NoSuchElementException ex, WebRequest request) {
        StringBuilder bodyOfResponse = new StringBuilder("Couldn't find the requested resource.");
        if (debug) {
            for (StackTraceElement line : ex.getStackTrace()) {
                bodyOfResponse.append("\n");
                bodyOfResponse.append(line.toString());
            }
        }
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
