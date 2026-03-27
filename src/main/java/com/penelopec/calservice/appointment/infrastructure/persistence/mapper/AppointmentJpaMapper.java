package com.penelopec.calservice.appointment.infrastructure.persistence.mapper;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;

public class AppointmentJpaMapper {

  private AppointmentJpaMapper() {
  }

  public static AppointmentJpaEntity toJpaEntity(Appointment appointment) {
    return new AppointmentJpaEntity(
      appointment.getId(),
      appointment.getBookingUid(),
      appointment.getEventTypeId(),
      appointment.getClientId(),
      appointment.getEstateAgentId(),
      appointment.getEstateId(),
      appointment.getDurationMinutes(),
      appointment.getStatus().name(),
      appointment.getStartDateTime(),
      appointment.getEndDateTime(),
      appointment.getCreatedAt(),
      appointment.getUpdatedAt()
    );
  }

  public static Appointment toDomain(AppointmentJpaEntity entity) {
    return Appointment.reconstitute(
      entity.getId(),
      entity.getBookingUid(),
      entity.getEventTypeId(),
      entity.getClientId(),
      entity.getEstateAgentId(),
      entity.getEstateId(),
      entity.getDurationMinutes(),
      Status.valueOf(entity.getStatus()),
      entity.getStartDateTime(),
      entity.getEndDateTime(),
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    );
  }
}
