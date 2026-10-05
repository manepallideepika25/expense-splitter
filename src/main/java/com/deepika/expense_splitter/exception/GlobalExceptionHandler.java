package com.deepika.expense_splitter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex)
    {
        ErrorResponse body= new ErrorResponse(
                LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation failed", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong", null);
        return ResponseEntity.internalServerError().body(body);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex)
    {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return ResponseEntity.badRequest().body(body);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Malformed request body (check field names, types and splitType)", null);
        return ResponseEntity.badRequest().body(body);
    }

}