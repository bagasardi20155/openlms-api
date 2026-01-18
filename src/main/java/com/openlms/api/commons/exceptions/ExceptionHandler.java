package com.openlms.api.commons.exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandler {
    
    public ResponseEntity<ErrorResponse> handleDomain(DomainException exception, HttpServletRequest request) {
        ErrorCode code = exception.getErrorCode();
        HttpStatus status = code.httpStatus();

        ErrorResponse body = new ErrorResponse(
            code.code(),
            exception.getMessage(),
            exception.getDetails(),
            status.value(),
            request.getRequestURI(),
            OffsetDateTime.now(),
            null
        );

        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse body = new ErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR.code(),
            "Uh Oh, Unexpected Error!",
            null,
            status.value(),
            request.getRequestURI(),
            OffsetDateTime.now(),
            null
        );

        return ResponseEntity.status(status).body(body);
    }
}
