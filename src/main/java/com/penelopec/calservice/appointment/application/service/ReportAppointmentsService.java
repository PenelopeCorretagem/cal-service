package com.penelopec.calservice.appointment.application.service;

import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput;
import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput.ClientInfo;
import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput.EstateAgentInfo;
import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput.EstateInfo;
import com.penelopec.calservice.appointment.application.output.AppointmentReportOutput.EventTypeInfo;
import com.penelopec.calservice.appointment.application.query.ReportAppointmentsQuery;
import com.penelopec.calservice.appointment.application.usecase.ReportAppointmentsUseCase;
import com.penelopec.calservice.appointment.application.util.AppointmentDateTimeParser;
import com.penelopec.calservice.appointment.domain.gateway.EstateData;
import com.penelopec.calservice.appointment.domain.gateway.EstateEnrichmentGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.domain.repository.AppointmentReportRow;
import com.penelopec.calservice.appointment.domain.repository.PageResult;
import com.penelopec.calservice.appointment.domain.valueobject.Status;
import com.penelopec.calservice.shared.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportAppointmentsService implements ReportAppointmentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReportAppointmentsService.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final AppointmentRepository repository;
    private final EstateEnrichmentGateway estateEnrichmentGateway;

    public ReportAppointmentsService(AppointmentRepository repository,
                                     EstateEnrichmentGateway estateEnrichmentGateway) {
        this.repository = repository;
        this.estateEnrichmentGateway = estateEnrichmentGateway;
    }

    @Override
    public Page<AppointmentReportOutput> execute(ReportAppointmentsQuery query) {
        int page = query.page() == null ? 0 : query.page();
        int size = Math.min(query.size() == null ? 20 : query.size(), MAX_PAGE_SIZE);
        Status status = parseStatus(query.status());
        LocalDateTime startDateTime = AppointmentDateTimeParser.parseOptional(query.startDateTime()).orElse(null);
        LocalDateTime endDateTime = AppointmentDateTimeParser.parseOptional(query.endDateTime()).orElse(null);

        List<EstateData> allEstates = estateEnrichmentGateway.fetchAll();
        Map<Long, EstateData> estateMap = allEstates.stream()
            .collect(Collectors.toMap(EstateData::id, e -> e));

        Set<Long> effectiveEstateIds = computeEffectiveEstateIds(
            query.estateId(), query.estateType(), allEstates);

        if (effectiveEstateIds != null && effectiveEstateIds.isEmpty()) {
            log.debug("Filtros contraditórios: estateId={} não pertence a estateType='{}'. Retornando página vazia.",
                query.estateId(), query.estateType());
            return new Page<>(List.of(), page, size, 0, 0);
        }

        PageResult<AppointmentReportRow> pageResult = repository.findForReport(
            query.clientId(), query.estateAgentId(), effectiveEstateIds,
            status, startDateTime, endDateTime, page, size);

        List<AppointmentReportOutput> items = pageResult.content().stream()
            .map(row -> toOutput(row, estateMap))
            .toList();

        return new Page<>(items, pageResult.page(), pageResult.size(),
            pageResult.totalElements(), pageResult.totalPages());
    }

    private Set<Long> computeEffectiveEstateIds(Long estateId, String estateType,
                                                List<EstateData> allEstates) {
        if (estateType == null || estateType.isBlank()) {
            return estateId != null ? Set.of(estateId) : null;
        }

        String normalizedKey = estateType.trim().toUpperCase();
        Set<Long> typeMatchingIds = allEstates.stream()
            .filter(e -> normalizedKey.equals(e.type()))
            .map(EstateData::id)
            .collect(Collectors.toSet());

        if (estateId != null) {
            return typeMatchingIds.contains(estateId) ? Set.of(estateId) : Set.of();
        }

        return typeMatchingIds;
    }

    private AppointmentReportOutput toOutput(AppointmentReportRow row, Map<Long, EstateData> estateMap) {
        EstateData estate = row.estateId() != null ? estateMap.get(row.estateId()) : null;

        EstateInfo estateInfo = buildEstateInfo(row.estateId(), estate);

        return new AppointmentReportOutput(
            row.id(),
            row.bookingUid(),
            row.eventTypeId(),
            row.clientId(),
            row.estateAgentId(),
            row.estateId(),
            row.durationMinutes(),
            row.status(),
            row.startDateTime(),
            row.endDateTime(),
            row.attendeeName(),
            row.attendeeEmail(),
            row.notes(),
            row.reason(),
            row.createdAt(),
            row.updatedAt(),
            estate != null ? estate.title() : null,
            estate != null ? estate.type() : null,
            row.eventTypeTitle(),
            new ClientInfo(row.clientId(), row.attendeeName(), row.attendeeEmail()),
            new EstateAgentInfo(row.estateAgentId(), null, null),
            estateInfo,
            new EventTypeInfo(row.eventTypeId(), row.eventTypeTitle())
        );
    }

    private EstateInfo buildEstateInfo(Long estateId, EstateData estate) {
        if (estate != null) {
            return new EstateInfo(estate.id(), estate.title(), estate.type());
        }
        return estateId != null ? new EstateInfo(estateId, null, null) : null;
    }

    private Status parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }
        return Status.valueOf(rawStatus.trim().toUpperCase());
    }
}
