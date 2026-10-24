package com.bths.platform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.stream.IntStream;

@Component
@Profile({"staging", "prod"})
public class ValidadorSegredoJwt {

    static final int TAMANHO_MINIMO_EM_BYTES = 32;
    static final int MINIMO_DE_BYTES_DISTINTOS = 16;

    public ValidadorSegredoJwt(@Value("${JWT_SECRET:}") String segredo) {
        validar(segredo);
    }

    static void validar(String segredoBase64) {

        if (segredoBase64 == null || segredoBase64.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET não foi definido."
            );
        }

        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(segredoBase64);

        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "JWT_SECRET deve estar codificado em Base64."
            );
        }

        if (bytes.length < TAMANHO_MINIMO_EM_BYTES) {
            throw new IllegalStateException(
                    "JWT_SECRET deve ter pelo menos 256 bits (32 bytes) depois de decodificado."
            );
        }

        long bytesDistintos = IntStream.range(0, bytes.length)
                .map(i -> bytes[i] & 0xFF)
                .distinct()
                .count();

        if (bytesDistintos < MINIMO_DE_BYTES_DISTINTOS) {
            throw new IllegalStateException(
                    "JWT_SECRET tem baixa entropia (bytes repetidos). Gere uma chave aleatória."
            );
        }
    }
}
