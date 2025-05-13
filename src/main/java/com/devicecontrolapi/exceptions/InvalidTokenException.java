package com.devicecontrolapi.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Token JWT inválido o expirado");
    }
}
