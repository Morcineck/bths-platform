package com.bths.platform.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String AUTH_COOKIE = "BTHS_TOKEN";
    private static final String CSRF_HEADER = "X-XSRF-TOKEN";


    @Bean
    public OpenAPI bthsOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("BTHS Platform API")
                                .version("v1")
                                .description(
                                        """
                                        API da BTHS Platform, sistema operacional e de experiência do hóspede da Beat Trips.

                                        A aplicação utiliza autenticação baseada em JWT armazenado em cookie HttpOnly.

                                        Para operações mutáveis protegidas por CSRF, o cliente deve inicializar o fluxo CSRF e enviar o token no header X-XSRF-TOKEN.

                                        Perfis principais:
                                        - ADMIN
                                        - STAFF
                                        - HOSPEDE
                                        """
                                )
                                .contact(
                                        new Contact()
                                                .name("Beat Trips")
                                )
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bthsCookieAuth",
                                        new SecurityScheme()
                                                .name(AUTH_COOKIE)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.COOKIE)
                                                .description(
                                                        "JWT de autenticação armazenado em cookie HttpOnly."
                                                )
                                )
                                .addSecuritySchemes(
                                        "csrfToken",
                                        new SecurityScheme()
                                                .name(CSRF_HEADER)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .description(
                                                        "Token CSRF necessário para operações mutáveis protegidas."
                                                )
                                )
                );
    }
}
