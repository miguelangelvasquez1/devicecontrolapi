package com.devicecontrolapi.controller;

import com.devicecontrolapi.dto.LoginRequest;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para registrar un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    // Endpoint para hacer login y obtener el token JWT - Cambiar a clase loginRequest?
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = usuarioService.loginAndGenerateToken(loginRequest.getEmail(), loginRequest.getClave());

        if (token != null) {
            // Si el login es exitoso, enviar el token como respuesta
            return ResponseEntity.ok(token);
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
    @GetMapping("/{id}") //@GetMapping("/{id:\\d+}"), Evita conflicto con "/login" asegurando que ID solo acepte números
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