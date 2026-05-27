package com.penelopec.calservice.appointment.application.usecase;

import com.penelopec.calservice.appointment.application.output.AvailableSlotsOutput;
import com.penelopec.calservice.appointment.application.query.GetAvailableSlotsQuery;

public interface GetAvailableSlotsUseCase {
  AvailableSlotsOutput execute(GetAvailableSlotsQuery query);
}
