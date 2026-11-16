package com.bths.platform.usuario;

import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioMapperTest {

    private final UsuarioMapper usuarioMapper = new UsuarioMapper();

    @Test
    void deveConverterUsuarioEmResponseComEmail() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("Staff QA");
        usuario.setEmail("staff@beattrips.com");
        usuario.setSenha("senha-criptografada");
        usuario.setPerfil(PerfilUsuario.STAFF);
        usuario.setAtivo(true);

        UsuarioResponse response = usuarioMapper.paraResponse(usuario);

        assertEquals(usuario.getId(), response.getId());
        assertEquals("Staff QA", response.getNome());
        assertEquals("staff@beattrips.com", response.getEmail());
        assertEquals(PerfilUsuario.STAFF, response.getPerfil());
        assertTrue(response.isAtivo());
    }
}