package com.fithub.fithub_api.inscricao.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InscricaoConflictException extends RuntimeException {
    public InscricaoConflictException(String message) {
        super(message);
    }
}
