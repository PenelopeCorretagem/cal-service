package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput;
import com.penelopec.calservice.appointment.application.query.ReportAppointmentsQuery;
import com.penelopec.calservice.shared.pagination.Page;

public interface ReportAppointmentsUseCase {

    Page<AppointmentReportOutput> execute(ReportAppointmentsQuery query);
}
