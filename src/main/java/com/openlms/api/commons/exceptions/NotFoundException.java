package com.openlms.api.commons.exceptions;

public class NotFoundException extends DomainException {
    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
