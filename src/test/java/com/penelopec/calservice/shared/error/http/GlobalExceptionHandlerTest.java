package com.penelopec.calservice.shared.error.http;

import com.penelopec.calservice.eventtype.infrastructure.config.SecurityFilter;
import com.penelopec.calservice.shared.error.core.CoreError;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.validation.ValidationError;
import com.penelopec.calservice.shared.validation.ValidationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
  controllers = GlobalExceptionHandlerTest.TestController.class,
  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
@Import({
  GlobalExceptionHandler.class,
  CoreHttpStatusRegister.class,
  GlobalExceptionHandlerTest.TestController.class
})
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturn422_whenApplicationValidationFails() throws Exception {
    mockMvc.perform(get("/test-errors/application-validation"))
      .andExpect(status().is(422))
      .andExpect(jsonPath("$.status").value(422))
      .andExpect(jsonPath("$.code").value(CoreError.VALIDATION_ERROR.code()))
      .andExpect(jsonPath("$.path").value("/test-errors/application-validation"))
      .andExpect(jsonPath("$.violations[0].field").value("email"))
      .andExpect(jsonPath("$.violations[0].code").value(CoreError.VALIDATION_ERROR.code()));
  }

  @Test
  void shouldReturn400_whenBeanValidationFails() throws Exception {
    mockMvc.perform(post("/test-errors/bean-validation")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.code").value(CoreError.VALIDATION_ERROR.code()))
      .andExpect(jsonPath("$.path").value("/test-errors/bean-validation"))
      .andExpect(jsonPath("$.violations[0].field").value("name"));
  }

  @Test
  void shouldReturn404_whenDomainExceptionIsThrown() throws Exception {
    mockMvc.perform(get("/test-errors/domain-not-found"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.code").value(CoreError.NOT_FOUND.code()));
  }

  @Test
  void shouldReturn502_whenGatewayExceptionIsThrown() throws Exception {
    mockMvc.perform(get("/test-errors/gateway-failure"))
      .andExpect(status().isBadGateway())
      .andExpect(jsonPath("$.status").value(502))
      .andExpect(jsonPath("$.code").value(CoreError.AUTH_GATEWAY_FAILED.code()));
  }

  @Test
  void shouldReturn400_whenPayloadIsMalformed() throws Exception {
    mockMvc.perform(post("/test-errors/bean-validation")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.code").value(CoreError.UNREADABLE_PAYLOAD.code()));
  }

  @Test
  void shouldReturn500_whenUnhandledExceptionOccurs() throws Exception {
    mockMvc.perform(get("/test-errors/fallback"))
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.status").value(500))
      .andExpect(jsonPath("$.code").value(CoreError.INTERNAL_ERROR.code()));
  }

  @Test
  void shouldReturn405_whenHttpMethodIsNotAllowed() throws Exception {
    mockMvc.perform(put("/test-errors/domain-not-found"))
      .andExpect(status().isMethodNotAllowed())
      .andExpect(jsonPath("$.status").value(405))
      .andExpect(jsonPath("$.code").value(CoreError.METHOD_NOT_ALLOWED.code()));
  }

  @RestController
  @RequestMapping("/test-errors")
  public static class TestController {

    @GetMapping("/application-validation")
    public String applicationValidation() {
      throw new ValidationException(List.of(
        ValidationError.of("email", CoreError.VALIDATION_ERROR)
      ));
    }

    @PostMapping("/bean-validation")
    public String beanValidation(@Valid @RequestBody SampleRequest request) {
      return request.name();
    }

    @GetMapping("/domain-not-found")
    public String domainNotFound() {
      throw new DomainException(CoreError.NOT_FOUND);
    }

    @GetMapping("/gateway-failure")
    public String gatewayFailure() {
      throw new GatewayException(CoreError.AUTH_GATEWAY_FAILED);
    }

    @GetMapping("/fallback")
    public String fallback() {
      throw new IllegalStateException("Falha inesperada de teste");
    }
  }

  public record SampleRequest(@NotBlank String name) {
  }
}