package com.fithub.fithub_api.aula.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AulaConflictException extends RuntimeException {
    public AulaConflictException(String message) {
        super(message);
    }
}
