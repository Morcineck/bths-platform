package com.bths.platform.usuario.dto;

import com.bths.platform.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UsuarioAtualizarRequest {

    @NotBlank(message = "O nome é obrigatório!")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O e-mail deve ser válido!")
    private String email;

    @NotNull(message = "O perfil é obrigatório!")
    private PerfilUsuario perfil;

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
