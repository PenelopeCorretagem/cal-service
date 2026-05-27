package com.penelopec.calservice.appointment.application.command;

public record RescheduleAppointmentCommand(
    Long appointmentId,
    String startDateTime,
    String reason) { }
