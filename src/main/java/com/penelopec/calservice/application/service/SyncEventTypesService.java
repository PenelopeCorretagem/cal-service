package com.penelopec.calservice.application.service;

import com.penelopec.calservice.application.port.in.SyncEventTypesUseCase;
import com.penelopec.calservice.domain.entity.EventType;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.domain.gateway.EstateData;
import com.penelopec.calservice.domain.gateway.EstateGateway;
import com.penelopec.calservice.domain.repository.EventTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SyncEventTypesService implements SyncEventTypesUseCase {

    private static final Logger log = LoggerFactory.getLogger(SyncEventTypesService.class);

    private final EstateGateway estateGateway;
    private final CalComEventTypeGateway calComGateway;
    private final EventTypeRepository eventTypeRepository;

    public SyncEventTypesService(EstateGateway estateGateway,
                                 CalComEventTypeGateway calComGateway,
                                 EventTypeRepository eventTypeRepository) {
        this.estateGateway = estateGateway;
        this.calComGateway = calComGateway;
        this.eventTypeRepository = eventTypeRepository;
    }

    @Override
    public void execute() {
        List<EstateData> estates = estateGateway.fetchAllEstates();
        List<EventType> existingEventTypes = eventTypeRepository.findAll();

        List<Long> duplicatedEstateIds = existingEventTypes.stream()
            .collect(Collectors.groupingBy(EventType::getEstateId, Collectors.counting()))
            .entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();

        if (!duplicatedEstateIds.isEmpty()) {
            throw new IllegalStateException(
                "Foram encontrados EventTypes duplicados por empreendimento: " + duplicatedEstateIds);
        }

        Map<Long, EventType> byEstateId = existingEventTypes.stream()
                .collect(Collectors.toMap(EventType::getEstateId, Function.identity(), (a, b) -> a));

        Set<Long> activeEstateIds = estates.stream()
                .filter(EstateData::active)
                .map(EstateData::id)
                .collect(Collectors.toSet());

        int created = 0, updated = 0, deactivated = 0;

        for (EstateData estate : estates) {
            if (!estate.active()) continue;

            try {
                EventType existing = byEstateId.get(estate.id());
                if (existing == null) {
                    createEventType(estate);
                    created++;
                } else if (updateEventTypeIfNeeded(estate, existing)) {
                    updated++;
                }
            } catch (Exception e) {
                log.error("Erro ao sincronizar empreendimento {}: {}", estate.id(), e.getMessage(), e);
            }
        }

        for (EventType eventType : existingEventTypes) {
            if (!activeEstateIds.contains(eventType.getEstateId()) && !eventType.isHidden()) {
                try {
                    deactivateEventType(eventType);
                    deactivated++;
                } catch (Exception e) {
                    log.error("Erro ao desativar EventType {} (empreendimento {}): {}",
                            eventType.getId(), eventType.getEstateId(), e.getMessage(), e);
                }
            }
        }

        log.info("Sincronização concluída — criados: {}, atualizados: {}, desativados: {}",
                created, updated, deactivated);
    }

    private void createEventType(EstateData estate) {
        EventType eventType = EventType.createNew(
                estate.name(), estate.description(), null, null, null, estate.id());
        EventType created = calComGateway.create(eventType, false);
        eventTypeRepository.save(created);
        log.info("EventType criado para empreendimento {}: calComId={}", estate.id(), created.getId());
    }

    private boolean updateEventTypeIfNeeded(EstateData estate, EventType existing) {
        boolean needsUpdate = false;

        if (!Objects.equals(existing.getTitle(), estate.name())) {
            existing.updateTitle(estate.name());
            needsUpdate = true;
        }
        if (!Objects.equals(existing.getDescription(), estate.description())) {
            existing.updateDescription(estate.description());
            needsUpdate = true;
        }
        if (existing.isHidden()) {
            existing.toggleHidden();
            needsUpdate = true;
        }

        if (needsUpdate) {
            calComGateway.update(existing, existing.isHidden());
            eventTypeRepository.save(existing);
            log.info("EventType atualizado para empreendimento {}: calComId={}", estate.id(), existing.getId());
        }

        return needsUpdate;
    }

    private void deactivateEventType(EventType eventType) {
        eventType.toggleHidden();
        calComGateway.update(eventType, eventType.isHidden());
        eventTypeRepository.save(eventType);
        log.info("EventType desativado: calComId={}, empreendimento={}", eventType.getId(), eventType.getEstateId());
    }
}
