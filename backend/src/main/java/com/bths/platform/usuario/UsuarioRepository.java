package com.bths.platform.usuario;

import com.bths.platform.usuario.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByPerfilInOrderByNomeAsc(List<PerfilUsuario>  perfil);

    boolean existsByEmailAndIdNot(String email, UUID id);

    long countByPerfilAndAtivoTrue(PerfilUsuario perfil);
}
