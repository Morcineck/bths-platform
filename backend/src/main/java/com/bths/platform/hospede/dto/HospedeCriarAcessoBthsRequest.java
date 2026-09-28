package com.bths.platform.hospede.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class HospedeCriarAcessoBthsRequest {

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O e-mail deve ser válido!")
    private String email;

    @NotBlank(message = "A senha temporária é obrigatória!")
    private String senhaTemporaria;

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getSenhaTemporaria() {
        return senhaTemporaria;
    }

    public void setSenhaTemporaria(
            String senhaTemporaria
    ) {
        this.senhaTemporaria =
                senhaTemporaria;
    }
}
