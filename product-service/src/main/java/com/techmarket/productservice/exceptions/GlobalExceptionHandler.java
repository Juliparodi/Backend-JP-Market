package com.techmarket.productservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;

import static com.techmarket.productservice.exceptions.constants.ExceptionConstants.PROMOTION_ALREADY_EXIST;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PromotionAlreadyExistException.class)
    public ResponseEntity<String> handleSuperHeroNotFoundException(PromotionAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(PROMOTION_ALREADY_EXIST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("holi");
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleValidationException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,})
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String errorMessage = fieldError.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
