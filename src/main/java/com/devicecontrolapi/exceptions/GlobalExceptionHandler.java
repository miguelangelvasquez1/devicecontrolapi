package com.devicecontrolapi.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // Verificar si la excepción es por un correo duplicado
        if (ex.getCause() instanceof SQLException) {
            SQLException sqlEx = (SQLException) ex.getCause();
            if (sqlEx.getErrorCode() == 1062) { // 1062 es el código de error de MySQL para "Duplicate entry"
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya está registrado.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error en el servidor.");
    }
}