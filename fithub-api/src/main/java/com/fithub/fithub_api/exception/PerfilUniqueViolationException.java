package com.fithub.fithub_api.exception;

public class PerfilUniqueViolationException extends RuntimeException {
    public PerfilUniqueViolationException(String message) {
        super(message);
    }
}
