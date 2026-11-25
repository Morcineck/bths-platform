package com.bths.platform.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRedefinirSenhaRequest(

        @NotBlank(message = "A nova senha é obrigatória!")
        @Size(min = 8, message = "A nova senha deve possuir pelo menos 8 caracteres!")
        String novaSenha
) {
}
