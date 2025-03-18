package com.devicecontrolapi.service;

import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Encriptar la contraseña usando BCrypt
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        String encryptedPassword = passwordEncoder.encode(usuario.getClave());
        usuario.setClave(encryptedPassword);
        return usuarioRepository.save(usuario);
    }

    // Verificar si las credenciales de login son correctas, retornar usuario?
    public boolean verificarLogin(String email, String clave) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            // Compara la contraseña en texto plano con la contraseña encriptada
            return passwordEncoder.matches(clave, usuario.getClave());
        }
        return false; // Usuario no encontrado
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Obtener un usuario por su id
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    // Obtener un usuario por su email
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Obtener usuarios por rol
    public List<Usuario> obtenerUsuariosPorRol(Byte rol) {
        return usuarioRepository.findByRol(rol);
    }

    // Buscar usuarios por nombre
    public List<Usuario> buscarUsuariosPorNombre(String nombre) {
        return usuarioRepository.findByNombreContaining(nombre);
    }

    // Guardar un nuevo usuario o actualizar uno existente
    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Eliminar un usuario por id
    public void eliminarUsuario(Integer id) {
        usuarioRepository.deleteById(id);
    }
}
