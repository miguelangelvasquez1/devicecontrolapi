package com.devicecontrolapi.communication.PasswordReset;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Transactional //Por el delete
    public String generateResetCode(String email) { //Genera y guarda el token de restablecimiento
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

    public boolean validateResetCode(String email, String code) { //Cuando pongo el código en la página
        return tokenRepository.findByEmailAndCode(email, code)
                .filter(token -> token.getExpiration().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
