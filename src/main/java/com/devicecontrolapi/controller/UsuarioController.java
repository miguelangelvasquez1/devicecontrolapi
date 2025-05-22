package com.devicecontrolapi.controller;

import com.devicecontrolapi.communication.RegisterConfirmation;
import com.devicecontrolapi.dto.LoginRequest;
import com.devicecontrolapi.dto.LoginResponseDTO;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
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

    // Endpoint para hacer login y obtener el token JWT - Cambiar a clase
    // loginRequest?
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponseDTO response = usuarioService.loginAndGenerateToken(
                loginRequest.getEmail(), loginRequest.getClave());

        if (response != null) {
            // Si el login es exitoso, enviar el DTO con el token
            return ResponseEntity.ok(response);
        } else {
            // Si las credenciales son incorrectas
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    // Obtener un usuario por su id, OPCIONAL?
    @GetMapping("/getById/{id:\\d+}") // @GetMapping("/{id:\\d+}"), Evita conflicto con "/login" asegurando que ID
                                      // solo acepte números
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Integer id) {
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
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}