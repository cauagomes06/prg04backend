package com.fithub.fithub_api.perfil.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PerfilUniqueViolationException extends RuntimeException {
    public PerfilUniqueViolationException(String message) {
        super(message);
    }
}
