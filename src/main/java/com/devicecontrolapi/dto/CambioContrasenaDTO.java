// src/main/java/com/devicecontrolapi/dto/CambioContrasenaDTO.java
package com.devicecontrolapi.dto;

public class CambioContrasenaDTO {
    private String currentPassword;
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
