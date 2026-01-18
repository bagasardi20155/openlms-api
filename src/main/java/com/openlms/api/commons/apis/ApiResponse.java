package com.openlms.api.commons.apis;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final OffsetDateTime timestamp;
    private final String traceId;

    protected ApiResponse(
        boolean success, 
        String message, 
        T data, 
        OffsetDateTime timestamp, 
        String traceId
    ) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data, OffsetDateTime.now(), null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, OffsetDateTime.now(), null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null, OffsetDateTime.now(), null);
    }

    public static <T> ApiResponse<T> fail(String message, String traceId) {
        return new ApiResponse<>(false, message, null, OffsetDateTime.now(), traceId);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public String getTraceId() {
        return traceId;
    }
}
