package com.devicecontrolapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProfileRequest {
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("telefono")
    private String telefono;

    // Constructor vacío
    public UpdateProfileRequest() {}

    // Constructor con parámetros
    public UpdateProfileRequest(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
