package com.courier.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 409 Conflict (duplicate username) ────────────────────
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateUsernameException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ── 401 Unauthorized (bad login) ─────────────────────────
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Map<String, Object>> handleLogin(InvalidLoginException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // ── 402 Payment Required ─────────────────────────────────
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePayment(PaymentException ex) {
        return error(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
    }

    // ── 503 No Employee Available ────────────────────────────
    @ExceptionHandler(NoEmployeeAvailableException.class)
    public ResponseEntity<Map<String, Object>> handleNoEmployee(NoEmployeeAvailableException ex) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    // ── 400 Validation errors ────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, msg);
    }

    // ── 400 Generic illegal argument ─────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegal(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── 500 Catch-all ────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage());
    }

    // ── Helper ───────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return ResponseEntity.status(status).body(body);
    }
}
