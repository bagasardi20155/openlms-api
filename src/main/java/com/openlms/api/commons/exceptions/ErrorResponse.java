package com.openlms.api.commons.exceptions;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final boolean success = false;
    private final String code;
    private final String message;
    private final Object details;
    private final int status;
    private final String path;
    private final OffsetDateTime timestamp;
    private final String traceId;

    public ErrorResponse(
        String code,
        String message,
        Object details,
        int status,
        String path,
        OffsetDateTime timestamp,
        String traceId
    ) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    public boolean isSuccess() { 
        return success; 
    }
    
    public String getCode() { 
        return code; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public Object getDetails() { 
        return details; 
    }
    
    public int getStatus() { 
        return status; 
    }
    
    public String getPath() { 
        return path; 
    }
    
    public String getTimestamp() { 
        return timestamp.toString(); 
    }
    
    public String getTraceId() { 
        return traceId; 
    }
}
