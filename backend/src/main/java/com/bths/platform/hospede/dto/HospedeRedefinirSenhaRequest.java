package com.bths.platform.hospede.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HospedeRedefinirSenhaRequest {

    @NotBlank(message = "A nova senha é obrigatória!")
    @Size(min = 8, message = "A nova senha deve possuir pelo menos 8 caracteres!")
    private String novaSenha;

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(
            String novaSenha
    ) {
        this.novaSenha =
                novaSenha;
    }
}