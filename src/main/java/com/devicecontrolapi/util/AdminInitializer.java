package com.devicecontrolapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.repository.UsuarioRepository;

@Component
public class AdminInitializer implements CommandLineRunner { //Spring Boot will automatically run its run method after loading the application context.

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    public AdminInitializer(UsuarioRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("admin");
            admin.setEmail("admin");
            admin.setClave(passwordEncoder.encode("admin123")); // Cámbialo en producción
            admin.setRol((byte) 4);
            userRepository.save(admin);
            System.out.println("✅ Admin por defecto creado: admin / admin123");
            log.info("✅ Usuario admin creado: {}", admin.getNombre());
        }
    }
}
