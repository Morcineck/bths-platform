package com.bths.platform.security;

import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
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


        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> me(
            Authentication authentication
    ) {


        UsuarioAutenticadoResponse response =
                authService.buscarUsuarioAutenticado(authentication.getName());

        return ResponseEntity.ok(response);
    }
}
