package com.penelopec.calservice.infrastructure.scheduler;

import com.penelopec.calservice.application.port.in.SyncEventTypesUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "scheduler.sync.enabled", havingValue = "true", matchIfMissing = true)
public class EventTypeSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventTypeSyncScheduler.class);

    private final SyncEventTypesUseCase syncEventTypesUseCase;

    public EventTypeSyncScheduler(SyncEventTypesUseCase syncEventTypesUseCase) {
        this.syncEventTypesUseCase = syncEventTypesUseCase;
    }

    @Scheduled(cron = "${scheduler.sync.cron:0 */5 * * * *}")
    public void syncEventTypes() {
        log.info("Iniciando sincronização de EventTypes com o monolito...");
        try {
            syncEventTypesUseCase.execute();
        } catch (Exception e) {
            log.error("Erro na sincronização de EventTypes: {}", e.getMessage(), e);
            throw e;
        }
    }
}
