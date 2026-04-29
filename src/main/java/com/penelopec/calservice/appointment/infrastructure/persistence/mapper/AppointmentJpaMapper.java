package com.penelopec.calservice.appointment.infrastructure.persistence.mapper;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;

public class AppointmentJpaMapper {

  private AppointmentJpaMapper() {}

  public static AppointmentJpaEntity toJpaEntity(Appointment appointment) {
    return new AppointmentJpaEntity(
      appointment.getId(),
      appointment.getBookingUid(),
      appointment.getEventTypeId(),
      appointment.getClientId(),
      appointment.getEstateAgentId(),
      appointment.getStatus().name(),
      appointment.getStartDateTime(),
      appointment.getEndDateTime(),
      appointment.getAttendeeName(),
      appointment.getAttendeeEmail(),
      appointment.getNotes(),
      appointment.getReason(),
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
      Status.valueOf(entity.getStatus()),
      entity.getStartDateTime(),
      entity.getEndDateTime(),
      entity.getAttendeeName(),
      entity.getAttendeeEmail(),
      entity.getNotes(),
      entity.getReason(),
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    );
  }
}
