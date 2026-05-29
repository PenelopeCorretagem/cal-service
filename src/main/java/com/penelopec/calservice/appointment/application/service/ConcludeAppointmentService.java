package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.command.ConcludeAppointmentCommand;
import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.ConcludeAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

public class ConcludeAppointmentService implements ConcludeAppointmentUseCase {

  private final AppointmentRepository repository;

  public ConcludeAppointmentService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
    @Caching(evict = {
      @CacheEvict(value = CacheNames.APPOINTMENTS, allEntries = true),
      @CacheEvict(value = CacheNames.AVAILABLE_SLOTS, allEntries = true),
      @CacheEvict(value = CacheNames.SCHEDULES, allEntries = true)
    }, put = {
      @CachePut(value = CacheNames.APPOINTMENT, key = "#command.appointmentId")
    })
  public AppointmentOutput execute(ConcludeAppointmentCommand command) {
    Appointment appointment = repository.findById(command.appointmentId())
      .orElseThrow(() -> new DomainException(AppointmentError.NOT_FOUND, command.appointmentId()));

    appointment.conclude();
    Appointment saved = repository.save(appointment);

    return AppointmentOutputMapper.toOutput(saved);
  }
}
