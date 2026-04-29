package com.penelopec.calservice.eventtype.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Cal.com Service",
    version = "1.0.0",
    description = "Microsserviço de gerenciamento de tipos de evento — integração com Cal.com"
  ),
  servers = {
    @Server(url = "/", description = "Servidor local")
  },
  tags = {
    @Tag(name = "Tipos de Evento", description = "Gerencia os tipos de evento via Cal.com")
  }
)
@SecurityScheme(
  name = "bearerAuth",
  type = SecuritySchemeType.HTTP,
  scheme = "bearer",
  bearerFormat = "JWT"
)
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }
}
