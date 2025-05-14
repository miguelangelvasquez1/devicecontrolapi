package com.devicecontrolapi.communication.PasswordReset;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devicecontrolapi.communication.PasswordResetSender;
import com.devicecontrolapi.dto.EmailPasswordResetRequest;
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

    @PostMapping("/forgot-password") //Enviar email para restablecer contraseña
    public ResponseEntity<?> forgotPassword(@RequestBody EmailPasswordResetRequest request) {
        String email = request.getEmail();
        String username = request.getName();

        if (!usuarioRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado");
        }

        String code = passwordResetService.generateResetCode(email);

        // Enviar correo usando Azure Function
        passwordResetSender.sendResetCode(email, username, code);

        return ResponseEntity.ok("Código de verificación enviado al correo.");
    }

    @PostMapping("/verify-code") //Verificar si el código es correcto
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean valid = passwordResetService.validateResetCode(email, code);
        if (!valid)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");

        return ResponseEntity.ok("Código válido, procede a restablecer tu contraseña.");
    }

    @Transactional //Por el delete del token
    @PostMapping("/reset-password") //Para cambiar la contraseña
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!passwordResetService.validateResetCode(request.getEmail(), request.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código inválido o expirado");
        } //Verifica de nuevo el código

        Usuario user = usuarioRepository.findByEmail(request.getEmail());
                // .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setClave(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(user); //Sobreescribe el usuario con la nueva contraseña

        tokenRepository.deleteByEmail(request.getEmail());

        return ResponseEntity.ok("Contraseña restablecida con éxito.");
    }

}