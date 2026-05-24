package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.ScheduleOutput;

import java.util.List;

public interface GetSchedulesUseCase {
  List<ScheduleOutput> execute();
}
