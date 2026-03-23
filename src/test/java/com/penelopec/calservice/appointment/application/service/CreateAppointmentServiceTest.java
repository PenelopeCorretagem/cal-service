package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.AppointmentCommand;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.BookingResult;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway.CreateBookingRequest;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentServiceTest {

  @Mock
  private CalComBookingGateway bookingGateway;

  @Mock
  private AppointmentRepository repository;

  @InjectMocks
  private CreateAppointmentService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve criar agendamento no gateway e persistir localmente")
    void shouldCreateAppointmentOnGatewayAndPersistLocally_whenCommandIsValid() {
      // Given
      AppointmentCommand command = new AppointmentCommand(
        101L,
        202L,
        303L,
        404L,
        "2026-03-22T14:00:00",
        "2026-03-22T15:00:00",
        "Cliente Teste",
        "cliente@teste.com",
        "Primeira visita"
      );

      BookingResult bookingResult = new BookingResult(
        "booking-uid-123",
        999L,
        "accepted",
        OffsetDateTime.parse("2026-03-22T14:00:00Z"),
        OffsetDateTime.parse("2026-03-22T15:00:00Z")
      );

      when(bookingGateway.createBooking(any(CreateBookingRequest.class))).thenReturn(bookingResult);
      when(repository.save(any(Appointment.class))).thenAnswer(invocation -> {
        Appointment appointment = invocation.getArgument(0);
        appointment.setId(10L);
        return appointment;
      });

      // When
      AppointmentOutput output = service.execute(command);

      // Then
      ArgumentCaptor<CreateBookingRequest> gatewayCaptor = ArgumentCaptor.forClass(CreateBookingRequest.class);
      ArgumentCaptor<Appointment> repositoryCaptor = ArgumentCaptor.forClass(Appointment.class);
      verify(bookingGateway).createBooking(gatewayCaptor.capture());
      verify(repository).save(repositoryCaptor.capture());

      CreateBookingRequest request = gatewayCaptor.getValue();
      assertThat(request.eventTypeId()).isEqualTo(101L);
      assertThat(request.startTime()).isEqualTo(LocalDateTime.parse("2026-03-22T14:00:00").atOffset(ZoneOffset.UTC));
      assertThat(request.endTime()).isEqualTo(LocalDateTime.parse("2026-03-22T15:00:00").atOffset(ZoneOffset.UTC));
      assertThat(request.attendeeName()).isEqualTo("Cliente Teste");
      assertThat(request.attendeeEmail()).isEqualTo("cliente@teste.com");
      assertThat(request.notes()).isEqualTo("Primeira visita");

      Appointment saved = repositoryCaptor.getValue();
      assertThat(saved.getBookingUid()).isEqualTo("booking-uid-123");
      assertThat(saved.getStatus()).isEqualTo(Status.PENDING);
      assertThat(saved.getDurationMinutes()).isEqualTo(60);

      assertThat(output.id()).isEqualTo(10L);
      assertThat(output.bookingUid()).isEqualTo("booking-uid-123");
      assertThat(output.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Deve lancar excecao quando startDateTime nao e informado")
    void shouldThrowException_whenStartDateTimeIsMissing() {
      // Given
      AppointmentCommand command = new AppointmentCommand(
        101L,
        202L,
        303L,
        404L,
        null,
        "2026-03-22T15:00:00",
        "Cliente Teste",
        "cliente@teste.com",
        "Primeira visita"
      );

      // When
      Throwable thrown = org.assertj.core.api.Assertions.catchThrowable(() -> service.execute(command));

      // Then
      assertThat(thrown)
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("startDateTime");
      verifyNoInteractions(bookingGateway, repository);
    }
  }
}
