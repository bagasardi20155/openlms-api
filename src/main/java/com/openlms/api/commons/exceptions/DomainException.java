package com.openlms.api.commons.exceptions;

import java.util.Map;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.code());
        this.errorCode = errorCode;
        this.details = null;
    }

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public DomainException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
