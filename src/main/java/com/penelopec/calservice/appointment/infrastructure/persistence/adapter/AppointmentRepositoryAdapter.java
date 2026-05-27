package com.penelopec.calservice.appointment.infrastructure.persistence.adapter;

import com.penelopec.calservice.appointment.domain.entity.Appointment;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.repository.PageResult;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.appointment.infrastructure.persistence.entity.AppointmentJpaEntity;
import com.penelopec.calservice.appointment.infrastructure.persistence.mapper.AppointmentJpaMapper;
import com.penelopec.calservice.appointment.infrastructure.persistence.repository.AppointmentJpaRepository;
import com.penelopec.calservice.eventtype.infrastructure.persistence.entity.EventTypeJpaEntity;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AppointmentRepositoryAdapter implements AppointmentRepository {

  private static final java.util.Set<String> TERMINAL_STATUSES =
    java.util.Set.of(Status.CANCELLED.name(), Status.CONCLUDED.name());

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
    return jpaRepository.findById(id).map(AppointmentJpaMapper::toDomain);
  }

  @Override
  public boolean existsActiveByEstateAgentAndStartDateTime(Long estateAgentId, LocalDateTime startDateTime) {
    return jpaRepository.existsByEstateAgentIdAndStartDateTimeAndStatusNotIn(
      estateAgentId,
      startDateTime,
      TERMINAL_STATUSES
    );
  }

  @Override
  public boolean existsActiveByEstateAgentAndStartDateTimeExcludingId(Long estateAgentId,
                                                                       LocalDateTime startDateTime,
                                                                       Long excludedId) {
    return jpaRepository.existsByEstateAgentIdAndStartDateTimeAndStatusNotInAndIdNot(
      estateAgentId,
      startDateTime,
      TERMINAL_STATUSES,
      excludedId
    );
  }

  @Override
  public PageResult<Appointment> findByFilters(Long clientId, Long estateAgentId, Long estateId,
                                               Status status, LocalDateTime startDate,
                                               LocalDateTime endDate, int page, int size) {
    Specification<AppointmentJpaEntity> spec = buildSpecification(
      clientId, estateAgentId, estateId, status, startDate, endDate);

    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<AppointmentJpaEntity> jpaPage = jpaRepository.findAll(spec, pageRequest);

    List<Appointment> content = jpaPage.getContent().stream()
      .map(AppointmentJpaMapper::toDomain)
      .toList();

    return new PageResult<>(content, jpaPage.getNumber(), jpaPage.getSize(),
      jpaPage.getTotalElements(), jpaPage.getTotalPages());
  }

  @Override
  public List<Appointment> findForExport(Long userId, LocalDateTime startDate,
                                         LocalDateTime endDate, Status status) {
    Specification<AppointmentJpaEntity> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (userId != null) {
        predicates.add(cb.equal(root.get("estateAgentId"), userId));
      }
      if (startDate != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("startDateTime"), startDate));
      }
      if (endDate != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("endDateTime"), endDate));
      }
      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status.name()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    return jpaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "startDateTime")).stream()
      .map(AppointmentJpaMapper::toDomain)
      .toList();
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  private Specification<AppointmentJpaEntity> buildSpecification(
      Long clientId, Long estateAgentId, Long estateId,
      Status status, LocalDateTime startDate, LocalDateTime endDate) {

    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (clientId != null) {
        predicates.add(cb.equal(root.get("clientId"), clientId));
      }
      if (estateAgentId != null) {
        predicates.add(cb.equal(root.get("estateAgentId"), estateAgentId));
      }
      if (status != null) {
        predicates.add(cb.equal(root.get("status"), status.name()));
      }
      if (startDate != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("startDateTime"), startDate));
      }
      if (endDate != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("endDateTime"), endDate));
      }
      if (estateId != null) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<EventTypeJpaEntity> eventType = subquery.from(EventTypeJpaEntity.class);
        subquery.select(eventType.get("id"))
          .where(cb.equal(eventType.get("estateId"), estateId));
        predicates.add(root.get("eventTypeId").in(subquery));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
