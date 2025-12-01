package com.fithub.fithub_api.plano.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PlanoUniqueViolationException extends RuntimeException {
    public PlanoUniqueViolationException(String message) {
        super(message);
    }
}
