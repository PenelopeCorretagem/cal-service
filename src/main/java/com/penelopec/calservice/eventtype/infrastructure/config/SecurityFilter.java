package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.auth.application.command.ValidateTokenCommand;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;
import com.penelopec.calservice.auth.application.port.in.ValidateTokenUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

  private final ValidateTokenUseCase validateTokenUseCase;

  public SecurityFilter(ValidateTokenUseCase validateTokenUseCase) {
    this.validateTokenUseCase = validateTokenUseCase;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    var token = recoverToken(request);

    if (token != null && !token.isBlank()) {
      try {
        var output = validateTokenUseCase.execute(new ValidateTokenCommand(token));

        if (isValidOutput(output)) {
          var authority = new SimpleGrantedAuthority("ROLE_" + output.accessLevel().toUpperCase(Locale.ROOT));
          String principal = output.id() != null ? output.id().toString() : output.email();
          var authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
          authentication.setDetails(output.email());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          SecurityContextHolder.clearContext();
          log.debug("Token validado sem identidade completa para path={}", request.getRequestURI());
        }
      } catch (Exception ex) {
        SecurityContextHolder.clearContext();
        // Token inválido ou authentication-service indisponível — segue sem autenticação.
        // Spring Security barrará a request se o endpoint exigir autenticação.
        log.warn("Falha ao validar token no authentication-service para path={}", request.getRequestURI());
        log.debug("Detalhes da falha ao validar token", ex);
      }
    }

    filterChain.doFilter(request, response);
  }

  private boolean isValidOutput(ValidateTokenOutput output) {
    return output != null
      && output.email() != null
      && !output.email().isBlank()
      && output.accessLevel() != null
      && !output.accessLevel().isBlank();
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
      return null;
    }

    String token = authHeader.substring(7).trim();
    return token.isBlank() ? null : token;
  }
}
