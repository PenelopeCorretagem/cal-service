package com.penelopec.calservice.appointment.application.command;

public record CancelAppointmentCommand(
    Long appointmentId,
    String reason) { }
