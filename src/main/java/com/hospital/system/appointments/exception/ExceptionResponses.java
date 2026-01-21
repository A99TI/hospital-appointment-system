package com.hospital.system.appointments.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponses {
    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}