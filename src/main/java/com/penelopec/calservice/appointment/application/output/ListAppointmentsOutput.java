package com.penelopec.calservice.appointment.application.output;

import java.util.List;

public record ListAppointmentsOutput(
	List<AppointmentOutput> appointments,
	int page,
	int size,
	long totalElements,
	int totalPages
) { }