package com.indra.reservations_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger/OpenAPI.
 * 
 * Configuración de la documentación API:
 * - Información general de la API
 * - Configuración de autenticación Bearer JWT
 * - Permite probar endpoints protegidos desde Swagger UI
 * 
 * Acceso:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs: http://localhost:8080/v3/api-docs
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Reservations API",
                version = "1.0",
                description = """
                        API REST para sistema de reservas de salas.
                        
                        **Autenticación:**
                        - La API utiliza JWT (JSON Web Tokens) para autenticación
                        - Obtén un token usando el endpoint POST /auth/login
                        - Usa el botón "Authorize" para configurar el token
                        - El token debe incluirse en el header Authorization como: Bearer {token}
                        
                        **Roles:**
                        - ADMIN: Acceso completo
                        - USUARIO: Acceso limitado a funciones de usuario
                        """,
                contact = @Contact(
                        name = "Indra",
                        email = "soporte@indra.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Servidor Local"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = """
                Ingresa el token JWT obtenido del endpoint /auth/login.
                
                Ejemplo:
                1. Haz POST a /auth/login con username y password
                2. Copia el token de la respuesta
                3. Click en "Authorize" arriba
                4. Pega el token (sin 'Bearer', solo el token)
                5. Click en "Authorize" y "Close"
                
                Ahora puedes probar los endpoints protegidos.
                """
)
public class SwaggerConfig {
    
    /**
     * La configuración se realiza mediante anotaciones.
     * No se requiere @Bean adicional con springdoc-openapi 2.x
     * 
     * Swagger UI estará disponible automáticamente en:
     * http://localhost:8080/swagger-ui.html
     * 
     * La documentación JSON estará en:
     * http://localhost:8080/v3/api-docs
     */
}
