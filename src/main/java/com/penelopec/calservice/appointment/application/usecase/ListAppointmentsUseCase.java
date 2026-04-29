package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.AppointmentOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;
import com.penelopec.calservice.shared.pagination.Page;

public interface ListAppointmentsUseCase {
    Page<AppointmentOutput> execute(ListAppointmentsQuery query);
}
