package com.bths.platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TrocaSenhaObrigatoriaFilter extends OncePerRequestFilter {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public TrocaSenhaObrigatoriaFilter(JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        String caminho = request.getRequestURI().substring(request.getContextPath().length());

        if (autenticacao != null
                && caminho.startsWith("/api/")
                && !caminho.startsWith("/api/auth/")
                && autenticacao.getAuthorities().stream().anyMatch(autoridade ->
                UsuarioDetailsService.AUTORIDADE_TROCA_SENHA_OBRIGATORIA.equals(autoridade.getAuthority()))) {

            jwtAccessDeniedHandler.handle(
                    request,
                    response,
                    new AccessDeniedException("Troca de senha obrigatória.")
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
