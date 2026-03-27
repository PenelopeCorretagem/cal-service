package com.penelopec.calservice.appointment.infrastructure.persistence.adapter;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;
import com.penelopec.calservice.appointment.infrastructure.persistence.mapper.AppointmentJpaMapper;
import com.penelopec.calservice.appointment.infrastructure.persistence.repository.AppointmentJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class AppointmentRepositoryAdapter implements AppointmentRepository {

  private final AppointmentJpaRepository jpaRepository;

  public AppointmentRepositoryAdapter(AppointmentJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Appointment save(Appointment appointment) {
    AppointmentJpaEntity entity = AppointmentJpaMapper.toJpaEntity(appointment);
    AppointmentJpaEntity saved = jpaRepository.save(entity);
    return AppointmentJpaMapper.toDomain(saved);
  }

  @Override
  public Optional<Appointment> findById(Long id) {
    return jpaRepository.findById(id)
      .map(AppointmentJpaMapper::toDomain);
  }

  @Override
  public Optional<Appointment> findByBookingUid(String bookingUid) {
    return jpaRepository.findByBookingUid(bookingUid)
      .map(AppointmentJpaMapper::toDomain);
  }

  @Override
  public List<Appointment> findAll() {
    return jpaRepository.findAll().stream()
      .map(AppointmentJpaMapper::toDomain)
      .toList();
  }

  @Override
  public List<Appointment> findByFilters(Long clientId, Long estateAgentId, Long estateId,
                                         Status status, LocalDateTime startDate,
                                         LocalDateTime endDate) {
    Stream<AppointmentJpaEntity> stream = jpaRepository.findAll().stream();

    if (clientId != null) {
      stream = stream.filter(e -> clientId.equals(e.getClientId()));
    }
    if (estateAgentId != null) {
      stream = stream.filter(e -> estateAgentId.equals(e.getEstateAgentId()));
    }
    if (estateId != null) {
      stream = stream.filter(e -> estateId.equals(e.getEstateId()));
    }
    if (status != null) {
      stream = stream.filter(e -> status.name().equals(e.getStatus()));
    }
    if (startDate != null) {
      stream = stream.filter(e -> !e.getStartDateTime().isBefore(startDate));
    }
    if (endDate != null) {
      stream = stream.filter(e -> !e.getEndDateTime().isAfter(endDate));
    }

    return stream.map(AppointmentJpaMapper::toDomain).toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}
