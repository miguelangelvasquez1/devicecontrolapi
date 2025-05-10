package com.devicecontrolapi.communication.PasswordReset;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    public String generateResetCode(String email) {
        // Elimina cualquier código anterior
        tokenRepository.deleteByEmail(email);

        String code = String.format("%06d", new Random().nextInt(999999));
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setCode(code);
        token.setExpiration(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(token);

        return code;
    }

    public boolean validateResetCode(String email, String code) {
        return tokenRepository.findByEmailAndCode(email, code)
                .filter(token -> token.getExpiration().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
