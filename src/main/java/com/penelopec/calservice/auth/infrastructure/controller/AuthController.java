package com.penelopec.calservice.auth.infrastructure.controller;

import com.penelopec.calservice.auth.application.command.LoginCommand;
import com.penelopec.calservice.auth.application.command.ValidateTokenCommand;
import com.penelopec.calservice.auth.application.port.in.AuthenticateUseCase;
import com.penelopec.calservice.auth.application.port.in.ValidateTokenUseCase;
import com.penelopec.calservice.auth.infrastructure.controller.dto.LoginRequest;
import com.penelopec.calservice.auth.infrastructure.controller.dto.LoginResponse;
import com.penelopec.calservice.auth.infrastructure.controller.dto.ValidateTokenRequest;
import com.penelopec.calservice.auth.infrastructure.controller.dto.ValidateTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Proxy para o microserviço de autenticação — use para obter o token JWT")
public class AuthController {

  private final AuthenticateUseCase authenticateUseCase;
  private final ValidateTokenUseCase validateTokenUseCase;

  public AuthController(AuthenticateUseCase authenticateUseCase, ValidateTokenUseCase validateTokenUseCase) {
    this.authenticateUseCase = authenticateUseCase;
    this.validateTokenUseCase = validateTokenUseCase;
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login (proxy → authentication-service)",
      description = "Encaminha as credenciais ao authentication-service e retorna o token JWT. "
          + "Use o token retornado no botão **Authorize** do Swagger (Bearer)."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = LoginResponse.class))),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
      @ApiResponse(responseCode = "502", description = "Falha ao conectar com authentication-service")
  })
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    var output = authenticateUseCase.execute(new LoginCommand(request.email(), request.password()));
    return ResponseEntity.ok(new LoginResponse(output.token(), output.userId(), output.accessLevel()));
  }

  @PostMapping("/validate-token")
  @Operation(
      summary = "Valida token JWT (proxy → authentication-service)",
      description = "Encaminha o token ao authentication-service e retorna os dados do usuário associado."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Token válido",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ValidateTokenResponse.class))),
      @ApiResponse(responseCode = "400", description = "Token não informado"),
      @ApiResponse(responseCode = "502", description = "Falha ao conectar com authentication-service")
  })
  public ResponseEntity<ValidateTokenResponse> validateToken(@Valid @RequestBody ValidateTokenRequest request) {
    var output = validateTokenUseCase.execute(new ValidateTokenCommand(request.token()));
    return ResponseEntity.ok(new ValidateTokenResponse(output.email(), output.id(), output.accessLevel()));
  }
}
