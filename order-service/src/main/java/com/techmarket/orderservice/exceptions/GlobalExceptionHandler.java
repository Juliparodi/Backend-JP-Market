package com.techmarket.orderservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static com.techmarket.orderservice.constants.ExceptionConstants.NO_INVENTORIES_MESSAGE;
import static com.techmarket.orderservice.constants.ExceptionConstants.NO_STOCK_MESSAGE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NoStockException.class, NoInventoriesException.class})
    public ResponseEntity<String> handleSuperHeroNotFoundException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleValidationException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String errorMessage = fieldError.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
