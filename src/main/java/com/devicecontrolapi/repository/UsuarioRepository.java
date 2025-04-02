package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Consulta para buscar un usuario por su email (ejemplo de método personalizado)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(@Param("email") String email);

    // Buscar usuarios por rol
    List<Usuario> findByRol(Byte rol);

    // Buscar usuarios por nombre (con búsqueda parcial)
    List<Usuario> findByNombreContaining(String nombre);

    boolean existsByEmail(String email);
}
