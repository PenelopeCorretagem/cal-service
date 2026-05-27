package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.ScheduleOutput;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.AvailabilityRule;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.OverrideRule;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.ScheduleResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSchedulesServiceTest {

  @Mock private CalComScheduleGateway scheduleGateway;
  @InjectMocks private GetSchedulesService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve retornar schedules do Cal.com com availability e overrides")
    void shouldReturnSchedulesWithAvailabilityAndOverrides() {
      var schedule = new ScheduleResult(
        254L,
        "Horário comercial",
        "America/Sao_Paulo",
        List.of(new AvailabilityRule(
          List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
          "09:00", "18:00"
        )),
        true,
        List.of(new OverrideRule("2026-05-01", "10:00", "14:00"))
      );
      when(scheduleGateway.getSchedules()).thenReturn(List.of(schedule));

      List<ScheduleOutput> result = service.execute();

      verify(scheduleGateway).getSchedules();
      assertThat(result).hasSize(1);

      ScheduleOutput output = result.get(0);
      assertThat(output.id()).isEqualTo(254L);
      assertThat(output.name()).isEqualTo("Horário comercial");
      assertThat(output.timeZone()).isEqualTo("America/Sao_Paulo");
      assertThat(output.isDefault()).isTrue();
      assertThat(output.availability()).hasSize(1);
      assertThat(output.availability().get(0).days()).containsExactly("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
      assertThat(output.availability().get(0).startTime()).isEqualTo("09:00");
      assertThat(output.availability().get(0).endTime()).isEqualTo("18:00");
      assertThat(output.overrides()).hasSize(1);
      assertThat(output.overrides().get(0).date()).isEqualTo("2026-05-01");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nao ha schedules")
    void shouldReturnEmptyListWhenNoSchedules() {
      when(scheduleGateway.getSchedules()).thenReturn(Collections.emptyList());

      List<ScheduleOutput> result = service.execute();

      assertThat(result).isEmpty();
    }
  }
}
