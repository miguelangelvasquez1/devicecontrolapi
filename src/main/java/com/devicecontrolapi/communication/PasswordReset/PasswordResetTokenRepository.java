package com.devicecontrolapi.communication.PasswordReset;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByEmailAndCode(String email, String code);
    
    void deleteByEmail(String email);
}