package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.ExportAppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ExportAppointmentsQuery;

import java.util.List;

public interface ExportAppointmentsUseCase {

  List<ExportAppointmentOutput> execute(ExportAppointmentsQuery query);
}
