package com.penelopec.calservice.shared.error.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ErrorHttpStatusMappingTest {

  @Test
  void shouldResolveInternalServerError_whenCodeIsNullOrBlank() {
    assertThat(ErrorHttpStatusMapping.resolve(null)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(ErrorHttpStatusMapping.resolve(" ")).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void shouldRegisterAndResolveStatus_whenCodeIsNew() {
    String code = uniqueCode();

    ErrorHttpStatusMapping.register(code, HttpStatus.BAD_GATEWAY);

    assertThat(ErrorHttpStatusMapping.resolve(code)).isEqualTo(HttpStatus.BAD_GATEWAY);
  }

  @Test
  void shouldAllowIdempotentRegistration_whenStatusIsTheSame() {
    String code = uniqueCode();
    ErrorHttpStatusMapping.register(code, HttpStatus.BAD_REQUEST);

    assertThatCode(() -> ErrorHttpStatusMapping.register(code, HttpStatus.BAD_REQUEST))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldRejectRegistration_whenCodeAlreadyHasDifferentStatus() {
    String code = uniqueCode();
    ErrorHttpStatusMapping.register(code, HttpStatus.BAD_REQUEST);

    assertThatThrownBy(() -> ErrorHttpStatusMapping.register(code, HttpStatus.NOT_FOUND))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("already registered");
  }

  @Test
  void shouldRejectRegistration_whenInputIsInvalid() {
    assertThatThrownBy(() -> ErrorHttpStatusMapping.register("", HttpStatus.OK))
      .isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> ErrorHttpStatusMapping.register(uniqueCode(), null))
      .isInstanceOf(IllegalArgumentException.class);
  }

  private String uniqueCode() {
    return "TEST-" + UUID.randomUUID();
  }
}