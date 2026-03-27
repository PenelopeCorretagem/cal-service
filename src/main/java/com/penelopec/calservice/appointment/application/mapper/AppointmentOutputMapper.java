package com.penelopec.calservice.appointment.application.mapper;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.domain.entity.Appointment;

public class AppointmentOutputMapper {

  private AppointmentOutputMapper() {
  }

  public static AppointmentOutput toOutput(Appointment appointment) {
    return new AppointmentOutput(
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
}
