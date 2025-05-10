package com.devicecontrolapi.communication.PasswordReset;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devicecontrolapi.communication.PasswordResetSender;
import com.devicecontrolapi.dto.PasswordResetRequest;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.repository.UsuarioRepository;

@RestController
@RequestMapping("/password-reset")
public class PasswordResetController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordResetSender passwordResetSender; // Llama a tu Azure Function

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // TODO: Verifica si el email existe en tu base de datos de usuarios

        String code = passwordResetService.generateResetCode(email);

        // Enviar correo usando Azure Function
        passwordResetSender.sendResetCode(email, code);

        return ResponseEntity.ok("Código de verificación enviado al correo.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean valid = passwordResetService.validateResetCode(email, code);
        if (!valid)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");

        return ResponseEntity.ok("Código válido, procede a restablecer tu contraseña.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!passwordResetService.validateResetCode(request.getEmail(), request.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");
        }

        Usuario user = usuarioRepository.findByEmail(request.getEmail());
                // .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setClave(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(user);

        tokenRepository.deleteByEmail(request.getEmail());

        return ResponseEntity.ok("Contraseña restablecida con éxito.");
    }

}