package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.ListAppointmentsOutput;
import com.penelopec.calservice.appointment.application.query.ListAppointmentsQuery;

public interface ListAppointmentsUseCase {
    ListAppointmentsOutput execute(ListAppointmentsQuery query);
}
