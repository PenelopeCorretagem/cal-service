package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.auth.application.command.ValidateTokenCommand;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;
import com.penelopec.calservice.auth.application.port.in.ValidateTokenUseCase;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

  @Mock
  private ValidateTokenUseCase validateTokenUseCase;

  @InjectMocks
  private SecurityFilter securityFilter;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldSetAuthentication_whenBearerTokenIsValid() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/event-types");
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    when(validateTokenUseCase.execute(new ValidateTokenCommand("valid-token")))
      .thenReturn(new ValidateTokenOutput("user@penelopec.com", 1L, "admin"));

    securityFilter.doFilterInternal(request, response, filterChain);

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getName()).isEqualTo("user@penelopec.com");
    assertThat(authentication.getAuthorities())
      .extracting(GrantedAuthority::getAuthority)
      .containsExactly("ROLE_ADMIN");
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotSetAuthentication_whenValidationOutputIsIncomplete() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/event-types");
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    when(validateTokenUseCase.execute(new ValidateTokenCommand("valid-token")))
      .thenReturn(new ValidateTokenOutput("", 1L, "administrador"));

    securityFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldContinueWithoutAuthentication_whenTokenValidationThrowsException() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/event-types");
    request.addHeader("Authorization", "Bearer invalid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    when(validateTokenUseCase.execute(new ValidateTokenCommand("invalid-token")))
      .thenThrow(new RuntimeException("Auth service indisponível"));

    securityFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotCallValidationUseCase_whenAuthorizationHeaderIsInvalid() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/event-types");
    request.addHeader("Authorization", "Basic abc123");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    securityFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verifyNoInteractions(validateTokenUseCase);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldSkipTokenValidation_whenAuthenticationAlreadyExists() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(
      new UsernamePasswordAuthenticationToken(
        "existing@penelopec.com",
        null,
        List.of(new SimpleGrantedAuthority("ROLE_USER"))
      )
    );

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/event-types");
    request.addHeader("Authorization", "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = mock(FilterChain.class);

    securityFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
      .isEqualTo("existing@penelopec.com");
    verifyNoInteractions(validateTokenUseCase);
    verify(filterChain).doFilter(request, response);
  }
}