
package net.iozamudioa.fueler.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.iozamudioa.fueler.infrastructure.adapters.in.web.admin.AdminRoutes;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String ADMIN_KEY = "adminKey";
    private static final String DEVICE_BEARER = "deviceBearer";

    @Bean
    public OpenAPI fuelerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fueler API")
                        .version("1.0.0")
                        .description(
                                "API multi-tenant para administración "
                                        + "de clientes, dispositivos, vehículos, "
                                        + "operadores, gasolineras y cargas "
                                        + "de combustible.")
                        .contact(new Contact()
                                .name("Fueler")))
                .schemaRequirement(
                        ADMIN_KEY,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Admin-Key")
                                .description(
                                        "Clave de acceso administrativo"))
                .schemaRequirement(
                        DEVICE_BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("Opaque")
                                .description(
                                        "Token asignado al dispositivo aprobado"));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("01-admin")
                .pathsToMatch(AdminRoutes.BASE + "/**")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(
                        new SecurityRequirement()
                                .addList(ADMIN_KEY)))
                .build();
    }

    @Bean
    public GroupedOpenApi deviceApi() {
        return GroupedOpenApi.builder()
                .group("02-device")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude(
                        AdminRoutes.BASE + "/**",
                        "/api/v1/devices")
                .addOpenApiCustomizer(openApi -> openApi.addSecurityItem(
                        new SecurityRequirement()
                                .addList(DEVICE_BEARER)))
                .build();
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("03-public")
                .pathsToMatch("/api/v1/devices")
                .build();
    }
}
