package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.ListAppointmentsOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAppointmentsServiceTest {

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private ListAppointmentsService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve retornar pagina filtrada quando query e valida")
    void shouldReturnPaginatedResult_whenQueryIsValid() {
      // Given
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        11L,
        22L,
        33L,
        "pending",
        "2026-03-22T00:00:00",
        "2026-03-23T00:00:00",
        1,
        2
      );

      when(repository.findByFilters(
        11L,
        22L,
        33L,
        Status.PENDING,
        LocalDateTime.parse("2026-03-22T00:00:00"),
        LocalDateTime.parse("2026-03-23T00:00:00")
      )).thenReturn(List.of(
        createAppointment(1L),
        createAppointment(2L),
        createAppointment(3L)
      ));

      // When
      ListAppointmentsOutput output = service.execute(query);

      // Then
      verify(repository).findByFilters(
        11L,
        22L,
        33L,
        Status.PENDING,
        LocalDateTime.parse("2026-03-22T00:00:00"),
        LocalDateTime.parse("2026-03-23T00:00:00")
      );
      assertThat(output.page()).isEqualTo(1);
      assertThat(output.size()).isEqualTo(2);
      assertThat(output.totalElements()).isEqualTo(3);
      assertThat(output.totalPages()).isEqualTo(2);
      assertThat(output.appointments()).hasSize(1);
      assertThat(output.appointments().get(0).id()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Deve aplicar pagina e tamanho padrao quando nao informados")
    void shouldApplyDefaultPageAndSize_whenNotProvided() {
      // Given
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
      );
      when(repository.findByFilters(null, null, null, null, null, null))
        .thenReturn(List.of(createAppointment(1L)));

      // When
      ListAppointmentsOutput output = service.execute(query);

      // Then
      assertThat(output.page()).isEqualTo(0);
      assertThat(output.size()).isEqualTo(20);
      assertThat(output.totalElements()).isEqualTo(1);
      assertThat(output.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve lancar excecao quando page e negativa")
    void shouldThrowException_whenPageIsNegative() {
      // Given
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null,
        null,
        null,
        null,
        null,
        null,
        -1,
        10
      );

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(query));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("page deve ser maior ou igual a zero");
      verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Deve lancar excecao quando status e invalido")
    void shouldThrowException_whenStatusIsInvalid() {
      // Given
      ListAppointmentsQuery query = new ListAppointmentsQuery(
        null,
        null,
        null,
        "nao-existe",
        null,
        null,
        0,
        10
      );

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(query));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("status");
      verifyNoInteractions(repository);
    }
  }

  private Appointment createAppointment(Long id) {
    return Appointment.reconstitute(
      id,
      "booking-" + id,
      11L,
      22L,
      33L,
      44L,
      60,
      Status.PENDING,
      LocalDateTime.parse("2026-03-22T10:00:00"),
      LocalDateTime.parse("2026-03-22T11:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00"),
      LocalDateTime.parse("2026-03-20T09:00:00")
    );
  }
}
