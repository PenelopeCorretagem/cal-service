package com.penelopec.calservice.shared.http.log;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingInterceptorTest {

  private final LoggingInterceptor interceptor = new LoggingInterceptor();

  @Test
  void shouldMaskSensitiveQueryParams_whenSanitizingUri() {
    URI uri = URI.create("https://api.cal.com/v2/bookings?token=abc&password=123&email=user@x.com");

    String sanitized = sanitize(uri);

    assertThat(sanitized).contains("token=***");
    assertThat(sanitized).contains("password=***");
    assertThat(sanitized).contains("email=user@x.com");
    assertThat(sanitized).doesNotContain("token=abc");
    assertThat(sanitized).doesNotContain("password=123");
  }

  @Test
  void shouldKeepUriUntouched_whenQueryHasNoSensitiveKeys() {
    URI uri = URI.create("https://api.cal.com/v2/bookings?email=user@x.com&name=joao");

    String sanitized = sanitize(uri);

    assertThat(sanitized).isEqualTo(uri.toString());
  }

  @Test
  void shouldReturnUnknown_whenUriIsNull() {
    String sanitized = sanitize(null);

    assertThat(sanitized).isEqualTo("unknown");
  }

  private String sanitize(URI uri) {
    return (String) ReflectionTestUtils.invokeMethod(interceptor, "sanitize", uri);
  }
}