package com.devicecontrolapi.service;

import com.devicecontrolapi.dto.LoginResponseDTO;
import com.devicecontrolapi.dto.UpdateProfileRequest;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.repository.UsuarioRepository;
import com.devicecontrolapi.util.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final JwtUtil jwtUtil;

    // Encriptar la contraseña usando BCrypt
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardarla
        String encryptedPassword = passwordEncoder.encode(usuario.getClave());// Instalar lombok si da error
        usuario.setClave(encryptedPassword);
        return usuarioRepository.save(usuario);
    }

    public boolean verificarLogin(String email, String clave) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            // Compara la contraseña en texto plano con la contraseña encriptada
            return passwordEncoder.matches(clave, usuario.getClave());
        }
        return false; // Usuario no encontrado
    }

    // Primero verifico que el correo y la contraseña sean correctos
    public LoginResponseDTO loginAndGenerateToken(String email, String clave) {
        boolean autenticado = verificarLogin(email, clave);
        if (autenticado) {
            Usuario usuario = usuarioRepository.findByEmail(email);

            // Generar JWT
            String token = jwtUtil.generateToken(email, usuario.getRol());

            // Crear respuesta
            LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
            loginResponseDTO.setIdusuario(usuario.getIdusuario());
            loginResponseDTO.setToken(token);
            loginResponseDTO.setNombre(usuario.getNombre());
            loginResponseDTO.setEmail(usuario.getEmail());
            loginResponseDTO.setRol(usuario.getRol());
            loginResponseDTO.setTelefono(usuario.getTelefono());

            return loginResponseDTO;
        } else {
            return null; // o lanza una excepción adecuada
        }
    }

    // NUEVO: Método para actualizar perfil
    public Usuario actualizarPerfil(Integer id, UpdateProfileRequest updateRequest) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Actualizar solo los campos permitidos
            if (updateRequest.getNombre() != null && !updateRequest.getNombre().trim().isEmpty()) {
                usuario.setNombre(updateRequest.getNombre().trim());
            }

            if (updateRequest.getTelefono() != null && !updateRequest.getTelefono().trim().isEmpty()) {
                // Convertir el teléfono a Long si es necesario
                try {
                    Long telefono = Long.parseLong(updateRequest.getTelefono().trim());
                    usuario.setTelefono(telefono.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("El teléfono debe ser un número válido");
                }
            }

            return usuarioRepository.save(usuario);
        }

        return null;
    }

    public void actualizarRol(Integer id, Byte nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id " + id));

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
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