package com.hospital.system.appointments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionResponses> handleResponseStatusException(ResponseStatusException exec){
        return buildResponseEntity(exec, HttpStatus.valueOf(exec.getStatusCode().value()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponses> handleException(Exception exec){
        return buildResponseEntity(exec, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ExceptionResponses> buildResponseEntity (Exception exec, HttpStatus status){
        ExceptionResponses error = new ExceptionResponses(
                status.value(),
                exec.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(error, status);
    }

}
