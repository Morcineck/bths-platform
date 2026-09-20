package com.bths.platform.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        String secret =
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

        jwtService = new JwtService(secret);

        userDetails = mock(UserDetails.class);
    }

    @Test
    void deveGerarTokenComEmailDoUsuario() {

        when(userDetails.getUsername())
                .thenReturn("admin@beattrips.com");

        String token =
                jwtService.gerarToken(userDetails);

        String emailExtraido =
                jwtService.extrairEmail(token);

        assertEquals(
                "admin@beattrips.com",
                emailExtraido
        );
    }

    @Test
    void deveConsiderarTokenValidoParaMesmoUsuario() {

        when(userDetails.getUsername())
                .thenReturn("admin@beattrips.com");

        String token =
                jwtService.gerarToken(userDetails);

        boolean valido =
                jwtService.tokenValido(token, userDetails);

        assertTrue(valido);
    }

    @Test
    void deveConsiderarTokenInvalidoParaOutroUsuario() {

        when(userDetails.getUsername())
                .thenReturn("admin@beattrips.com");

        String token =
                jwtService.gerarToken(userDetails);

        UserDetails outroUsuario =
                mock(UserDetails.class);

        when(outroUsuario.getUsername())
                .thenReturn("staff@beattrips.com");

        boolean valido =
                jwtService.tokenValido(token, outroUsuario);

        assertFalse(valido);
    }

    @Test
    void deveRejeitarTokenExpirado() {

        String secret =
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

        byte[] chaveDecodificada =
                Base64.getDecoder().decode(secret);

        SecretKey chave =
                Keys.hmacShaKeyFor(chaveDecodificada);

        long agora = System.currentTimeMillis();

        String tokenExpirado = Jwts.builder()
                .subject("admin@beattrips.com")
                .issuedAt(new Date(agora - 7200000))
                .expiration(new Date(agora - 3600000))
                .signWith(chave)
                .compact();

        when(userDetails.getUsername())
                .thenReturn("admin@beattrips.com");

        assertFalse(
                jwtService.tokenValido(tokenExpirado, userDetails)
        );
    }
}