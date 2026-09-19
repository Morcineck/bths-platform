package com.bths.platform.usuario.mapper;

import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.dto.UsuarioResponse;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse paraResponse(Usuario usuario)
    {
        UsuarioResponse response = new UsuarioResponse();

        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setPerfil(usuario.getPerfil());
        response.setAtivo(usuario.isAtivo());

        return response;

    }
}
