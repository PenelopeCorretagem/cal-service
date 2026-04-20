package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.application.validator.ListAppointmentsQueryValidator;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.repository.PageResult;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.pagination.Page;
import com.penelopec.calservice.shared.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAppointmentsServiceTest {

  @Mock private AppointmentRepository repository;
  @Spy private ListAppointmentsQueryValidator queryValidator = new ListAppointmentsQueryValidator();
  @InjectMocks private ListAppointmentsService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve retornar pagina filtrada quando query e valida")
    void shouldReturnPaginatedResult_whenQueryIsValid() {
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        11L, 22L, 33L, "pending",
        "2026-03-22T00:00:00", "2026-03-23T00:00:00", 0, 2
      );
      when(repository.findByFilters(
        11L, 22L, 33L, Status.PENDING,
        LocalDateTime.parse("2026-03-22T00:00:00"),
        LocalDateTime.parse("2026-03-23T00:00:00"),
        0, 2
      )).thenReturn(new PageResult<>(
        List.of(createAppointment(1L), createAppointment(2L)),
        0, 2, 3, 2
      ));

      Page<AppointmentOutput> output = service.execute(query);

      assertThat(output.page()).isEqualTo(0);
      assertThat(output.size()).isEqualTo(2);
      assertThat(output.totalElements()).isEqualTo(3);
      assertThat(output.totalPages()).isEqualTo(2);
      assertThat(output.content()).hasSize(2);
    }

    @Test
    @DisplayName("Deve aplicar pagina e tamanho padrao quando nao informados")
    void shouldApplyDefaultPageAndSize_whenNotProvided() {
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null, null, null, null, null, null, null, null
      );
      when(repository.findByFilters(null, null, null, null, null, null, 0, 20))
        .thenReturn(new PageResult<>(List.of(createAppointment(1L)), 0, 20, 1, 1));

      Page<AppointmentOutput> output = service.execute(query);

      assertThat(output.page()).isEqualTo(0);
      assertThat(output.size()).isEqualTo(20);
      assertThat(output.totalElements()).isEqualTo(1);
      assertThat(output.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve lancar excecao quando page e negativa")
    void shouldThrowException_whenPageIsNegative() {
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null, null, null, null, null, null, -1, 10
      );

      assertThatThrownBy(() -> service.execute(query))
        .isInstanceOf(ValidationException.class)
        .satisfies(throwable -> {
          ValidationException ex = (ValidationException) throwable;
          assertThat(ex.getErrors()).anySatisfy(error -> {
            assertThat(error.field()).isEqualTo("page");
            assertThat(error.code()).isEqualTo(AppointmentError.LIST_PAGE_INVALID.code());
          });
        });
      verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lancar excecao quando size excede o maximo")
    void shouldThrowException_whenSizeExceedsMax() {
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null, null, null, null, null, null, 0, 101
      );

      assertThatThrownBy(() -> service.execute(query))
        .isInstanceOf(ValidationException.class)
        .satisfies(throwable -> {
          ValidationException ex = (ValidationException) throwable;
          assertThat(ex.getErrors()).anySatisfy(error -> {
            assertThat(error.field()).isEqualTo("size");
            assertThat(error.code()).isEqualTo(AppointmentError.LIST_SIZE_INVALID.code());
          });
        });
      verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lancar excecao quando status e invalido")
    void shouldThrowException_whenStatusIsInvalid() {
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null, null, null, "nao-existe", null, null, 0, 10
      );

      assertThatThrownBy(() -> service.execute(query))
        .isInstanceOf(ValidationException.class)
        .satisfies(throwable -> {
          ValidationException ex = (ValidationException) throwable;
          assertThat(ex.getErrors()).anySatisfy(error -> {
            assertThat(error.field()).isEqualTo("status");
            assertThat(error.code()).isEqualTo(AppointmentError.LIST_STATUS_INVALID.code());
          });
        });
      verifyNoInteractions(repository);
    }
  }

  private Appointment createAppointment(Long id) {
    return Appointment.reconstitute(
      id, "booking-" + id, 11L, 22L, 33L,
      Status.PENDING,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      "Cliente Teste", "cliente@teste.com", "Notas", null,
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
