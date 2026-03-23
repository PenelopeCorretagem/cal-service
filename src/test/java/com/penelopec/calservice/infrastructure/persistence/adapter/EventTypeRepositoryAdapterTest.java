package com.penelopec.calservice.infrastructure.persistence.adapter;

import com.penelopec.calservice.eventtype.domain.entity.EventType;
import com.penelopec.calservice.eventtype.infrastructure.persistence.adapter.EventTypeRepositoryAdapter;
import com.penelopec.calservice.eventtype.infrastructure.persistence.entity.EventTypeJpaEntity;
import com.penelopec.calservice.eventtype.infrastructure.persistence.repository.EventTypeJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventTypeRepositoryAdapterTest {

  @Mock
  private EventTypeJpaRepository jpaRepository;

  @InjectMocks
  private EventTypeRepositoryAdapter adapter;

  @Nested
  @DisplayName("save")
  class Save {

    @Test
    @DisplayName("Deve mapear para JPA e retornar domínio")
    void shouldMapToJpaAndReturnDomain() {
      // Given
      EventType domain = EventType.reconstitute(10L, "Visita A", "visita-a", "Desc", 60, 120, false, 90L);
      EventTypeJpaEntity savedEntity = new EventTypeJpaEntity(10L, "Visita A", "visita-a", "Desc", 60, 120, false, 90L);
      when(jpaRepository.save(org.mockito.ArgumentMatchers.any(EventTypeJpaEntity.class))).thenReturn(savedEntity);

      // When
      EventType result = adapter.save(domain);

      // Then
      assertThat(result.getId()).isEqualTo(10L);
      assertThat(result.getTitle()).isEqualTo("Visita A");
      assertThat(result.getEstateId()).isEqualTo(90L);
    }
  }

  @Nested
  @DisplayName("finders")
  class Finders {

    @Test
    @DisplayName("Deve buscar por ID")
    void shouldFindById() {
      // Given
      Long id = 1L;
      EventTypeJpaEntity entity = new EventTypeJpaEntity(id, "Visita", "visita", "Desc", 60, 120, false, 7L);
      when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

      // When
      Optional<EventType> result = adapter.findById(id);

      // Then
      assertThat(result).isPresent();
      assertThat(result.get().getSlugValue()).isEqualTo("visita");
    }

    @Test
    @DisplayName("Deve buscar por empreendimento")
    void shouldFindByEstateId() {
      // Given
      Long estateId = 100L;
      EventTypeJpaEntity entity = new EventTypeJpaEntity(11L, "Visita", "visita", "Desc", 60, 120, false, estateId);
      when(jpaRepository.findByEstateId(estateId)).thenReturn(Optional.of(entity));

      // When
      Optional<EventType> result = adapter.findByEstateId(estateId);

      // Then
      assertThat(result).isPresent();
      assertThat(result.get().getEstateId()).isEqualTo(estateId);
    }

    @Test
    @DisplayName("Deve listar todos")
    void shouldListAll() {
      // Given
      EventTypeJpaEntity first = new EventTypeJpaEntity(1L, "A", "a", "Desc", 60, 120, false, 1L);
      EventTypeJpaEntity second = new EventTypeJpaEntity(2L, "B", "b", "Desc", 45, 90, true, 2L);
      when(jpaRepository.findAll()).thenReturn(List.of(first, second));

      // When
      List<EventType> results = adapter.findAll();

      // Then
      assertThat(results).hasSize(2);
      assertThat(results.get(0).getId()).isEqualTo(1L);
      assertThat(results.get(1).isHidden()).isTrue();
    }
  }

  @Test
  @DisplayName("deleteById deve delegar para repositório JPA")
  void shouldDelegateDeleteById() {
    // Given
    Long id = 55L;

    // When
    adapter.deleteById(id);

    // Then
    verify(jpaRepository).deleteById(id);
  }
}
