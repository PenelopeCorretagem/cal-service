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
      appointment.getDurationMinutes(),
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
}
