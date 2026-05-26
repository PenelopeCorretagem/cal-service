package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.eventtype.infrastructure.config.properties.CorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CorsProperties corsProperties;

  public SecurityConfig(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
  }

  private static final String[] AUTH_WHITELIST = {
    "/error",

    // Auth proxy
    "/auth/login",
    "/auth/validate-token",

    // Swagger
    "/swagger-ui/**",
    "/api-docs/**",
    "/swagger-resources/**",
    "/webjars/**",

    // H2
    "/h2-console/**"
  };

  @Bean
  @Profile("prod")
  public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http, SecurityFilter securityFilter) throws Exception {
    return http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
        .requestMatchers(AUTH_WHITELIST).permitAll()
        .requestMatchers(HttpMethod.DELETE, "/appointments/**").hasAnyRole("ADMINISTRADOR", "CORRETOR")
        .requestMatchers(HttpMethod.GET, "/appointments/export").hasRole("ADMINISTRADOR")
        .anyRequest().authenticated()
      )
      .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
      .build();
  }

  @Bean
  @Profile("dev")
  public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
      .cors(Customizer.withDefaults())
      .csrf(AbstractHttpConfigurer::disable)
      .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(authorize -> authorize
        .anyRequest().permitAll()
      )
      .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(corsProperties.allowedOrigins());
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
