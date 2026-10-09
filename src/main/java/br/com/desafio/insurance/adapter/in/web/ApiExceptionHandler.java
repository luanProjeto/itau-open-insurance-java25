package br.com.desafio.insurance.adapter.in.web;

import br.com.desafio.insurance.domain.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<?> notFound(NotFoundException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class, ObjectOptimisticLockingFailureException.class})
    ResponseEntity<?> conflict(Exception e) {
        return error(HttpStatus.CONFLICT, "Conflito ao processar apólice");
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    ResponseEntity<?> badRequest(Exception e) {
        return error(HttpStatus.BAD_REQUEST, "Requisição inválida: " + e.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now().toString(), "status", status.value(), "error", status.getReasonPhrase(), "message", message));
    }
}
