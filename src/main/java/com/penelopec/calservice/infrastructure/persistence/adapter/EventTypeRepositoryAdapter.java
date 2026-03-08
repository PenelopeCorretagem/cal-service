package com.penelopec.calservice.infrastructure.persistence.adapter;


import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.repository.EventTypeRepository;
import com.penelopec.calservice.infrastructure.persistence.entity.EventTypeJpaEntity;
import com.penelopec.calservice.infrastructure.persistence.mapper.EventTypeJpaMapper;
import com.penelopec.calservice.infrastructure.persistence.repository.EventTypeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventTypeRepositoryAdapter implements EventTypeRepository {

  private final EventTypeJpaRepository jpaRepository;

  public EventTypeRepositoryAdapter(EventTypeJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public EventType save(EventType eventType) {
    EventTypeJpaEntity entity = EventTypeJpaMapper.toJpaEntity(eventType);
    EventTypeJpaEntity saved = jpaRepository.save(entity);
    return EventTypeJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<EventType> findById(Long id) {
    return jpaRepository.findById(id)
      .map(EventTypeJpaMapper::toDomain);
  }

  @Override
  public Optional<EventType> findByEstateId(Long estateId) {
    return jpaRepository.findByEstateId(estateId)
      .map(EventTypeJpaMapper::toDomain);
  }

  @Override
  public List<EventType> findAll() {
    return jpaRepository.findAll().stream()
      .map(EventTypeJpaMapper::toDomain)
      .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}
