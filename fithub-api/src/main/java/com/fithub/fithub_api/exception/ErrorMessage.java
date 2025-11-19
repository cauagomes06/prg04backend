package com.fithub.fithub_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
public class ErrorMessage {

    private String path;
    private String method;
    private int status;
    private String statusText;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL) // Só aparece se houver erros de validação
    private Map<String, String> errors;

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
    }

    // Construtor especial para erros de validação (@Valid)
    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
        addErrors(result);
    }

    private void addErrors(BindingResult result) {
        this.errors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            this.errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }
}