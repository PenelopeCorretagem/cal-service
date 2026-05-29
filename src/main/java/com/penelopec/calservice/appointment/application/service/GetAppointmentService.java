package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.mapper.AppointmentOutputMapper;
import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.usecase.GetAppointmentUseCase;
import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.shared.error.core.DomainException;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import org.springframework.cache.annotation.Cacheable;

public class GetAppointmentService implements GetAppointmentUseCase {

  private final AppointmentRepository repository;

  public GetAppointmentService(AppointmentRepository repository) {
    this.repository = repository;
  }

  @Override
  @Cacheable(value = CacheNames.APPOINTMENT, key = "#id")
  public AppointmentOutput execute(Long id) {
    Appointment appointment = repository.findById(id)
      .orElseThrow(() -> new DomainException(AppointmentError.NOT_FOUND, id));

    return AppointmentOutputMapper.toOutput(appointment);
  }
}
