package com.devicecontrolapi.communication.PasswordReset;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
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

    @PostMapping("/forgot-password") // Enviar email para restablecer contraseña
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> email) {

        String emailValue = email.get("email");
        Usuario user = usuarioRepository.findByEmail(emailValue);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", "Correo no registrado"));
        }

        String username = user.getNombre();

        String code = passwordResetService.generateResetCode(emailValue);

        // Enviar correo usando Azure Function
        passwordResetSender.sendResetCode(emailValue, username, code); // Esto puede no enviarse, mostrar mensaje en el front

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("message", "Código de verificación enviado al correo."));
    }

    @PostMapping("/verify-code") // Verificar si el código es correcto
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean valid = passwordResetService.validateResetCode(email, code);
        if (!valid)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");

        return ResponseEntity.ok("Código válido, procede a restablecer tu contraseña.");
    }

    @Transactional // Por el delete del token
    @PostMapping("/reset-password") // Para cambiar la contraseña
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!passwordResetService.validateResetCode(request.getEmail(), request.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");
        } // Verifica de nuevo el código

        Usuario user = usuarioRepository.findByEmail(request.getEmail());
        // .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setClave(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(user); // Sobreescribe el usuario con la nueva contraseña

        tokenRepository.deleteByEmail(request.getEmail());

        return ResponseEntity.ok("Contraseña restablecida con éxito.");
    }

}