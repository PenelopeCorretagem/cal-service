package com.penelopec.calservice.infrastructure.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  @Value("${app.security.token.secret}")
  private String secret;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    var token = recoverToken(request);

    if (token != null) {
      try {
        var verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        var decodedJWT = verifier.verify(token);
        var subject = decodedJWT.getSubject();

        if (subject != null) {
          var authentication = new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (JWTVerificationException ignored) {
        // Token inválido — segue sem autenticação
      }
    }

    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
  }
}
