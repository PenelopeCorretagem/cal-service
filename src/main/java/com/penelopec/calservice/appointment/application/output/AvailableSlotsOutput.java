package com.penelopec.calservice.appointment.application.output;

import java.util.List;
import java.util.Map;

public record AvailableSlotsOutput(
  Map<String, List<String>> slots
) {}
