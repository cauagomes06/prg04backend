package com.fithub.fithub_api.infraestructure.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fithub.fithub_api.aula.exception.AulaConflictException;
import com.fithub.fithub_api.exception.EntityNotFoundException;
import com.fithub.fithub_api.exception.ErrorMessage;
import com.fithub.fithub_api.inscricao.exception.InscricaoConflictException;
import com.fithub.fithub_api.perfil.exception.PerfilUniqueViolationException;
import com.fithub.fithub_api.plano.exception.PlanoUniqueViolationException;
import com.fithub.fithub_api.reserva.exception.ReservaUniqueException;
import com.fithub.fithub_api.usuario.exception.CpfUniqueViolationException;
import com.fithub.fithub_api.usuario.exception.PasswordInvalidException;
import com.fithub.fithub_api.usuario.exception.UsernameUniqueViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Sobrescreve o método padrão para capturar erros de validação (@Valid) e customiza o retorno
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("Erro de validação", ex);

        BindingResult result = ex.getBindingResult();
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        ErrorMessage error = new ErrorMessage(
                servletRequest,
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Campo(s) inválido(s)",
                result
        );

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    // Sobrescreve o método padrão para capturar JSON inválido (ex: erro de parse) e customiza a mensagem
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("Erro ao interpretar JSON", ex);

        String message = "O formato dos dados enviados é inválido.";
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        if (ex.getCause() instanceof InvalidFormatException ie) {
            message = "Tipo inválido para o campo: " + ie.getValue();
        }

        ErrorMessage error = new ErrorMessage(servletRequest, HttpStatus.BAD_REQUEST, message);

        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
    }

    // Captura erros de integridade do banco (ex: chaves únicas duplicadas)
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

    // Captura exceção de entidade não encontrada no banco
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {

        log.error("Não encontrado", ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, ex.getMessage()));
    }



    //excecoes conflict
    @ExceptionHandler({
            PerfilUniqueViolationException.class,
            AulaConflictException.class,
            InscricaoConflictException.class,
            ReservaUniqueException.class,
            PlanoUniqueViolationException.class,
            UsernameUniqueViolationException.class,
            CpfUniqueViolationException.class,
            EntityEmUsoException.class

    })
    public ResponseEntity<ErrorMessage> handleConflictRuleErrors(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Conflito de regra de negócio", ex);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, ex.getMessage()));
    }
    // Captura exceções personalizadas de regras de negócio
    @ExceptionHandler({
            PasswordInvalidException.class,
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

    // Captura erros de permissão de acesso (usuário logado sem permissão)
    @ExceptionHandler({
            AccessDeniedException.class})
    public ResponseEntity<ErrorMessage> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.error("Acesso negado", ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, "Acesso negado."));
    }
    @ExceptionHandler(PlanoDataViolation.class)
    public ResponseEntity<ErrorMessage> handlePlanoViolation(
            RuntimeException ex,
            HttpServletRequest request) {

        log.error("Violação de data de plano", ex);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    // Captura falhas na autenticação (usuário não logado ou token inválido)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ErrorMessage> handleAuthException(
            org.springframework.security.core.AuthenticationException ex,
            HttpServletRequest request) {

        log.error("Autenticação falhou", ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Falha na autenticação."));
    }

    // Captura qualquer outro erro inesperado (Genérico)
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