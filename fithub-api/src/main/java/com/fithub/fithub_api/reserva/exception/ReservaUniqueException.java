package com.fithub.fithub_api.reserva.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReservaUniqueException extends RuntimeException {
    public ReservaUniqueException(String message) {
        super(message);
    }
}
