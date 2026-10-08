package com.bths.platform.security;

import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Login, logout, sessão autenticada e inicialização do token CSRF.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Realizar login",
            description = "Autentica o usuário por e-mail e senha e grava o JWT no cookie HttpOnly BTHS_TOKEN.")
    @ApiResponses({@ApiResponse(responseCode = "200",
            description = "Login realizado com sucesso. O JWT é enviado no cookie BTHS_TOKEN."),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha inválidos.")})
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest request,
            HttpServletResponse httpResponse
    ) {

        LoginResponse response = authService.login(request);

        ResponseCookie cookie = ResponseCookie
                .from("BTHS_TOKEN", response.getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();
        httpResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );


        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Consultar usuário autenticado",
            description = "Retorna os dados básicos da conta atualmente autenticada.",
            security = {@SecurityRequirement(name = "bthsCookieAuth")})
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Usuário autenticado retornado com sucesso.",
            content = @Content(schema = @Schema(implementation = UsuarioAutenticadoResponse.class))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.")})
    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> me(
            Authentication authentication
    ) {


        UsuarioAutenticadoResponse response =
                authService.buscarUsuarioAutenticado(authentication.getName());

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Encerrar sessão",
            description = "Remove o cookie de autenticação BTHS_TOKEN e encerra a sessão do usuário.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido em /api/auth/csrf.", required = true)
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Sessão encerrada com sucesso.")})
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse httpResponse
    ) {

        ResponseCookie cookie = ResponseCookie
                .from("BTHS_TOKEN", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        httpResponse.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Inicializar token CSRF",
            description = "Inicializa o fluxo CSRF e retorna os dados do token que devem ser utilizados pelo" +
                    " cliente em operações mutáveis.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Token CSRF inicializado com sucesso.",
            content = @Content(schema = @Schema(implementation = CsrfToken.class)))})
    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> csrf(
            CsrfToken csrfToken
    ) {

        return ResponseEntity.ok(csrfToken);
    }
}
