package com.devicecontrolapi.controller;

import com.devicecontrolapi.communication.RegisterConfirmation;
import com.devicecontrolapi.dto.CambioContrasenaDTO;
import com.devicecontrolapi.dto.LoginRequest;
import com.devicecontrolapi.dto.LoginResponseDTO;
import com.devicecontrolapi.dto.UpdateProfileRequest; // Nuevo DTO
import com.devicecontrolapi.dto.UsuarioRolUpdate;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200") // Permitir CORS para Angular
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RegisterConfirmation registerConfirmation;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Object> registrarUsuario(@RequestBody Usuario usuario) {

        if (usuarioService.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ya está registrado.");
        }
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        registerConfirmation.realizarLlamadaHttp(usuario.getEmail(), usuario.getNombre(), usuario.rolNumberToString());
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Endpoint para hacer login y obtener el token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponseDTO response = usuarioService.loginAndGenerateToken(
                loginRequest.getEmail(), loginRequest.getClave());

        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // NUEVO: Endpoint para actualizar perfil del usuario
    @PutMapping("/perfil/{id}")
    public ResponseEntity<?> actualizarPerfil(@PathVariable("id") Integer id,
            @RequestBody UpdateProfileRequest updateRequest) {
        try {
            Usuario usuarioActualizado = usuarioService.actualizarPerfil(id, updateRequest);
            if (usuarioActualizado != null) {
                return ResponseEntity.ok(usuarioActualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el perfil: " + e.getMessage());
        }
    }

    @PutMapping("/rolUpdate/{id}")
    public ResponseEntity<?> actualizarRolUsuario(@PathVariable("id") Integer id, @RequestBody UsuarioRolUpdate request) {
        if (request.getRol() == null) {
            return ResponseEntity.badRequest().body("El rol no puede ser nulo");
        }

        usuarioService.actualizarRol(id, request.getRol());
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    // Obtener un usuario por su id
    @GetMapping("/getById/{id:\\d+}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable("id") Integer id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener un usuario por email
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> obtenerUsuarioPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    // Obtener usuarios por rol
    @GetMapping("/rol/{rol}")
    public List<Usuario> obtenerUsuariosPorRol(@PathVariable Byte rol) {
        return usuarioService.obtenerUsuariosPorRol(rol);
    }

    // Buscar usuarios por nombre
    @GetMapping("/buscarPorNombre")
    public List<Usuario> buscarUsuariosPorNombre(@RequestParam String nombre) {
        return usuarioService.buscarUsuariosPorNombre(nombre);
    }

    // Crear o actualizar un usuario
    @PostMapping
    public Usuario guardarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.guardarUsuario(usuario);
    }

    // Eliminar un usuario por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

     @PostMapping("/cambiar-contrasena/{id}")
public ResponseEntity<?> cambiarContrasena(@PathVariable Integer id, @RequestBody CambioContrasenaDTO dto) {
    try {
        usuarioService.cambiarContrasena(id, dto);
        return ResponseEntity.ok(Collections.singletonMap("message", "Contraseña actualizada correctamente"));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(Collections.singletonMap("message", "Error al cambiar la contraseña"));
    }
}
}