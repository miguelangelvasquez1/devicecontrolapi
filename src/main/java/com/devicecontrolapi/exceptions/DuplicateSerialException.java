package com.devicecontrolapi.exceptions;

public class DuplicateSerialException extends RuntimeException {

   public DuplicateSerialException() {
    super("Serial ya existente");
   } 
}
