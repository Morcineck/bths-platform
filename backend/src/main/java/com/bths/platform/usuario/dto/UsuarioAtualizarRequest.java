package com.bths.platform.usuario.dto;

import com.bths.platform.usuario.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioAtualizarRequest(

    @NotBlank(message = "O nome é obrigatório!")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O e-mail deve ser válido!")
    String email,

    @NotNull(message = "O perfil é obrigatório!")
    PerfilUsuario perfil

) {
}
