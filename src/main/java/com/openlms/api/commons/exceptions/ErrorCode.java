package com.openlms.api.commons.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND"),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT");

    private final HttpStatus httpStatus;
    private final String code;

    ErrorCode(HttpStatus httpStatus, String code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }

    public String code() {
        return code;
    }
}
