package com.devicecontrolapi.exceptions;

public class TokenMissingException extends RuntimeException{

    public TokenMissingException() {
        super("Token JWT faltante o mal formado");
    }    
}
