package com.bths.platform.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;

    public JwtService(@Value("${JWT_SECRET}") String secret) {
        byte[] chaveDecodificada = Base64.getDecoder().decode(secret);
        this.chave = Keys.hmacShaKeyFor(chaveDecodificada);
    }

    public String gerarToken(UserDetails usuario) {

        long agora = System.currentTimeMillis();
        long expiracao = agora + (60 * 60 * 1000);

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(new Date(agora))
                .expiration(new Date(expiracao))
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

    }

    public boolean tokenValido(String token, UserDetails usuario) {
        String email = extrairEmail(token);

        return email.equals(usuario.getUsername())
                && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    private Date extrairExpiracao(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }
}

