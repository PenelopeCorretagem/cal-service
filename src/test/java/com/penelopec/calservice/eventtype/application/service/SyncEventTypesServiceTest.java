package com.penelopec.calservice.eventtype.application.service;

import com.penelopec.calservice.eventtype.application.port.in.ChangeEventTypeUseCase;
import com.penelopec.calservice.eventtype.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.domain.gateway.AdvertisementGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.EstateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SyncEventTypesServiceTest {

  @Mock
  private AdvertisementGateway advertisementGateway;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @Mock
  private CreateEventTypeUseCase createEventTypeUseCase;

  @Mock
  private ChangeEventTypeUseCase changeEventTypeUseCase;

  @InjectMocks
  private SyncEventTypesService service;

  @Nested
  @DisplayName("execute")
  class Execute {

    @Test
    @DisplayName("Deve criar EventType para advertisement sem vínculo existente")
    void shouldCreateEventTypeForAdvertisementWithoutExistingBinding() {
      // Given
      AdvertisementResponse advertisement = new AdvertisementResponse(
        true, new EstateResponse(101L, "Empreendimento A", "Desc A", null, null)
      );

      when(advertisementGateway.fetchAllAdvertisements()).thenReturn(List.of(advertisement));
      when(eventTypeRepository.findByEstateId(101L)).thenReturn(Optional.empty());

      // When
      service.execute();

      // Then
      verify(createEventTypeUseCase).execute(any());
    }

    @Test
    @DisplayName("Deve atualizar EventType quando título ou descrição mudarem")
    void shouldUpdateEventTypeWhenTitleOrDescriptionChanges() {
      // Given
      AdvertisementResponse advertisement = new AdvertisementResponse(
        true, new EstateResponse(202L, "Nome Novo", "Descrição Nova", null, null)
      );
      EventType existing = EventType.reconstitute(88L, "Nome Antigo", "nome-antigo", "Descrição Antiga", 60, 120, false, 202L);

      when(advertisementGateway.fetchAllAdvertisements()).thenReturn(List.of(advertisement));
      when(eventTypeRepository.findByEstateId(202L)).thenReturn(Optional.of(existing));

      // When
      service.execute();

      // Then
      verify(changeEventTypeUseCase).execute(any());
    }

    @Test
    @DisplayName("Não deve atualizar EventType quando dados forem iguais")
    void shouldNotUpdateEventTypeWhenDataIsUnchanged() {
      // Given
      AdvertisementResponse advertisement = new AdvertisementResponse(
        false, new EstateResponse(303L, "Mesmo Nome", "Mesma Desc", null, null)
      );
      EventType existing = EventType.reconstitute(44L, "Mesmo Nome", "mesmo-nome", "Mesma Desc", 60, 120, false, 303L);

      when(advertisementGateway.fetchAllAdvertisements()).thenReturn(List.of(advertisement));
      when(eventTypeRepository.findByEstateId(303L)).thenReturn(Optional.of(existing));

      // When
      service.execute();

      // Then
      verify(changeEventTypeUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Deve lançar GatewayException quando gateway falhar")
    void shouldThrowGatewayExceptionWhenGatewayFails() {
      // Given
      when(advertisementGateway.fetchAllAdvertisements()).thenThrow(new RuntimeException("connection refused"));

      // When / Then
      assertThatThrownBy(() -> service.execute())
        .isInstanceOf(Exception.class);
    }
  }
}
