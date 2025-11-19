package com.fithub.fithub_api.infraestructure.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fithub.fithub_api.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Validação (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.error("Erro de validação", ex);

        BindingResult result = ex.getBindingResult();

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessage(
                        request,
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Campo(s) inválido(s)",
                        result
                ));
    }


    // 2. JSON inválido (ex: Long esperado, veio String)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleJsonParseError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.error("Erro ao interpretar JSON", ex);

        String message = "O formato dos dados enviados é inválido.";

        if (ex.getCause() instanceof InvalidFormatException ie) {
            message = "Tipo inválido para o campo: " + ie.getValue();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, message));
    }


    // 3. Violação de integridade
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Violação de integridade", ex);

        String message = "Operação não permitida. Violação de integridade.";

        if (ex.getMessage() != null && ex.getMessage().contains("foreign key")) {
            message = "Não é possível excluir este item pois ele está em uso.";
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, message));
    }


    // 4. Não encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.error("Não encontrado", ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }


    // 5. Regras de negócio
    @ExceptionHandler({
            UsernameUniqueViolationException.class,
            CpfUniqueViolationException.class,
            AulaConflictException.class,
            InscricaoConflictException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorMessage> handleBusinessErrors(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Erro de regra de negócio", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, ex.getMessage()));
    }


    // 6. Segurança
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.error("Acesso negado", ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN,
                        "Acesso negado."));
    }


    // 7. Autenticação
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthException(
            org.springframework.security.core.AuthenticationException ex,
            HttpServletRequest request) {

        log.error("Autenticação falhou", ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED,
                        "Falha na autenticação."));
    }


    // 8. Erro genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGenericError(
            Exception ex,
            HttpServletRequest request) {

        log.error("Erro inesperado", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(
                        request,
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno no servidor."
                ));
    }
}