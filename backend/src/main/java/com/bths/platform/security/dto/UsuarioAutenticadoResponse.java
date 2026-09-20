package com.bths.platform.security.dto;

import com.bths.platform.usuario.enums.PerfilUsuario;

import java.util.UUID;

public class UsuarioAutenticadoResponse {

    private UUID id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;

    public UsuarioAutenticadoResponse(
            UUID id,
            String nome,
            String email,
            PerfilUsuario perfil
    )  {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
}
