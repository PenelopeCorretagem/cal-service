package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.usecase.DeleteAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

public class DeleteAppointmentService implements DeleteAppointmentUseCase {

  private final CalComBookingGateway bookingGateway;
  private final AppointmentRepository repository;

  public DeleteAppointmentService(CalComBookingGateway bookingGateway,
                                  AppointmentRepository repository) {
    this.bookingGateway = bookingGateway;
    this.repository = repository;
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = CacheNames.APPOINTMENT, key = "#id"),
      @CacheEvict(value = CacheNames.APPOINTMENTS, allEntries = true),
      @CacheEvict(value = CacheNames.AVAILABLE_SLOTS, allEntries = true),
      @CacheEvict(value = CacheNames.SCHEDULES, allEntries = true)
  })
  public void execute(Long id) {
    Appointment appointment = repository.findById(id)
      .orElseThrow(() -> new DomainException(AppointmentError.NOT_FOUND, id));

    if (appointment.getBookingUid() != null) {
      bookingGateway.cancelBooking(appointment.getBookingUid(), "Removido pelo sistema");
    }

    repository.deleteById(id);
  }
}
