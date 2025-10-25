package com.pismo.account.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pismo Account Service API",
                version = "1.0.0",
                description = "REST API for managing customer accounts and transactions with JWT authentication",
                contact = @Contact(
                        name = "Pismo",
                        email = "support@pismo.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.pismo.com", description = "Production Server")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT authentication token. Login via /api/auth/login to get your token, then click 'Authorize' and enter: Bearer {token}",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
