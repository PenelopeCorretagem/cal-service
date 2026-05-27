package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.AvailableSlotsOutput;
import com.penelopec.calservice.appointment.application.query.GetAvailableSlotsQuery;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.AvailableSlotsRequest;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway.SlotResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAvailableSlotsServiceTest {

  @Mock private CalComScheduleGateway scheduleGateway;
  @InjectMocks private GetAvailableSlotsService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve retornar slots disponíveis agrupados por data")
    void shouldReturnSlotsGroupedByDate() {
      var query = new GetAvailableSlotsQuery(100L, "2026-04-28", "2026-04-29");

      Map<String, List<SlotResult>> gatewayResult = new LinkedHashMap<>();
      gatewayResult.put("2026-04-28", List.of(
        new SlotResult("2026-04-28T09:00:00-03:00"),
        new SlotResult("2026-04-28T10:00:00-03:00")
      ));
      gatewayResult.put("2026-04-29", List.of(
        new SlotResult("2026-04-29T14:00:00-03:00")
      ));
      when(scheduleGateway.getAvailableSlots(any())).thenReturn(gatewayResult);

      AvailableSlotsOutput result = service.execute(query);

      ArgumentCaptor<AvailableSlotsRequest> captor = ArgumentCaptor.forClass(AvailableSlotsRequest.class);
      verify(scheduleGateway).getAvailableSlots(captor.capture());

      AvailableSlotsRequest request = captor.getValue();
      assertThat(request.eventTypeId()).isEqualTo(100L);
      assertThat(request.start()).isEqualTo("2026-04-28");
      assertThat(request.end()).isEqualTo("2026-04-29");
      assertThat(request.timeZone()).isEqualTo("America/Sao_Paulo");

      assertThat(result.slots()).hasSize(2);
      assertThat(result.slots().get("2026-04-28")).containsExactly(
        "2026-04-28T09:00:00-03:00", "2026-04-28T10:00:00-03:00"
      );
      assertThat(result.slots().get("2026-04-29")).containsExactly(
        "2026-04-29T14:00:00-03:00"
      );
    }

    @Test
    @DisplayName("Deve retornar mapa vazio quando nao ha slots")
    void shouldReturnEmptyMapWhenNoSlots() {
      var query = new GetAvailableSlotsQuery(100L, "2026-04-28", "2026-04-29");
      when(scheduleGateway.getAvailableSlots(any())).thenReturn(Collections.emptyMap());

      AvailableSlotsOutput result = service.execute(query);

      assertThat(result.slots()).isEmpty();
    }
  }
}
