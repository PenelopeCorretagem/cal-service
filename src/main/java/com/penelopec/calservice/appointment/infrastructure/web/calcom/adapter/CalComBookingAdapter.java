package com.penelopec.calservice.appointment.infrastructure.web.calcom.adapter;

import com.penelopec.calservice.appointment.domain.exception.AppointmentIntegrationException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComBookingRequest;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComBookingResponse;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComCancelRequest;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.dto.CalComRescheduleRequest;
import com.penelopec.calservice.eventtype.infrastructure.web.calcom.dto.CalComApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Optional;

public class CalComBookingAdapter implements CalComBookingGateway {

  private static final Logger log = LoggerFactory.getLogger(CalComBookingAdapter.class);

  private final RestClient restClient;

  private static final ParameterizedTypeReference<CalComApiResponse<CalComBookingResponse>>
    WRAPPER_BOOKING = new ParameterizedTypeReference<>() {};

  public CalComBookingAdapter(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  public BookingResult createBooking(CreateBookingRequest request) {
    log.info("Criando booking no Cal.com para eventTypeId={}", request.eventTypeId());

    CalComBookingRequest body = CalComBookingRequest.of(
      request.eventTypeId(),
      request.startTime(),
      request.endTime(),
      request.attendeeName(),
      request.attendeeEmail(),
      request.notes()
    );

    try {
      CalComBookingResponse response = Optional.ofNullable(
        restClient.post()
          .uri("/v2/bookings")
          .body(body)
          .retrieve()
          .body(WRAPPER_BOOKING))
        .map(CalComApiResponse::data)
        .orElseThrow(() -> new AppointmentIntegrationException(
          "Resposta nula do Cal.com ao criar booking"));

      log.info("Booking criado no Cal.com: uid={}", response.uid());
      return toResult(response);
    } catch (AppointmentIntegrationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Erro ao criar booking no Cal.com: {}", e.getMessage(), e);
      throw new AppointmentIntegrationException("Falha ao criar booking no Cal.com", e);
    }
  }

  @Override
  public BookingResult rescheduleBooking(String bookingUid, OffsetDateTime newStartTime,
                                         OffsetDateTime newEndTime, String reason) {
    log.info("Reagendando booking uid={} no Cal.com", bookingUid);

    CalComRescheduleRequest body = new CalComRescheduleRequest(newStartTime, newEndTime, reason);

    try {
      CalComBookingResponse response = Optional.ofNullable(
        restClient.patch()
          .uri("/v2/bookings/{uid}/reschedule", bookingUid)
          .body(body)
          .retrieve()
          .body(WRAPPER_BOOKING))
        .map(CalComApiResponse::data)
        .orElseThrow(() -> new AppointmentIntegrationException(
          "Resposta nula do Cal.com ao reagendar booking " + bookingUid));

      log.info("Booking {} reagendado no Cal.com", bookingUid);
      return toResult(response);
    } catch (AppointmentIntegrationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Erro ao reagendar booking {} no Cal.com: {}", bookingUid, e.getMessage(), e);
      throw new AppointmentIntegrationException("Falha ao reagendar booking no Cal.com", e);
    }
  }

  @Override
  public BookingResult cancelBooking(String bookingUid, String reason) {
    log.info("Cancelando booking uid={} no Cal.com", bookingUid);

    CalComCancelRequest body = new CalComCancelRequest(reason, false);

    try {
      CalComBookingResponse response = Optional.ofNullable(
        restClient.post()
          .uri("/v2/bookings/{uid}/cancel", bookingUid)
          .body(body)
          .retrieve()
          .body(WRAPPER_BOOKING))
        .map(CalComApiResponse::data)
        .orElseThrow(() -> new AppointmentIntegrationException(
          "Resposta nula do Cal.com ao cancelar booking " + bookingUid));

      log.info("Booking {} cancelado no Cal.com", bookingUid);
      return toResult(response);
    } catch (AppointmentIntegrationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Erro ao cancelar booking {} no Cal.com: {}", bookingUid, e.getMessage(), e);
      throw new AppointmentIntegrationException("Falha ao cancelar booking no Cal.com", e);
    }
  }

  @Override
  public BookingResult getBooking(String bookingUid) {
    log.info("Buscando booking uid={} no Cal.com", bookingUid);

    try {
      CalComBookingResponse response = Optional.ofNullable(
        restClient.get()
          .uri("/v2/bookings/{uid}", bookingUid)
          .retrieve()
          .body(WRAPPER_BOOKING))
        .map(CalComApiResponse::data)
        .orElseThrow(() -> new AppointmentIntegrationException(
          "Booking não encontrado no Cal.com: " + bookingUid));

      return toResult(response);
    } catch (AppointmentIntegrationException e) {
      throw e;
    } catch (Exception e) {
      log.error("Erro ao buscar booking {} no Cal.com: {}", bookingUid, e.getMessage(), e);
      throw new AppointmentIntegrationException("Falha ao buscar booking no Cal.com", e);
    }
  }

  private BookingResult toResult(CalComBookingResponse response) {
    return new BookingResult(
      response.uid(),
      response.id(),
      response.status(),
      response.startTime(),
      response.endTime()
    );
  }
}
