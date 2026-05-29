package com.penelopec.calservice.eventtype.infrastructure.scheduler;

import com.penelopec.calservice.eventtype.application.port.in.SyncEventTypesUseCase;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.CalcomProperties;
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
  private final CalcomProperties calcomProperties;

  public EventTypeSyncScheduler(SyncEventTypesUseCase syncEventTypesUseCase,
                                CalcomProperties calcomProperties) {
    this.syncEventTypesUseCase = syncEventTypesUseCase;
    this.calcomProperties = calcomProperties;
  }

  @Scheduled(cron = "${scheduler.sync.cron:0 */5 * * * *}")
  public void syncEventTypes() {
    if (!isCalComTokenConfigured()) {
      log.warn("Sincronização ignorada: CALCOM_API_KEY ausente ou com valor placeholder.");
      return;
    }

    log.info("Iniciando sincronização de EventTypes com o monolito...");
    try {
      syncEventTypesUseCase.execute();
    } catch (Exception e) {
      log.error("Erro na sincronização de EventTypes: {}", e.getMessage(), e);
    }
  }

  private boolean isCalComTokenConfigured() {
    String apiKey = calcomProperties.api() != null ? calcomProperties.api().key() : null;
    if (apiKey == null) {
      return false;
    }

    String normalized = apiKey.trim();
    return !normalized.isEmpty()
      && !normalized.equalsIgnoreCase("troque_pela_sua_chave_calcom")
      && !normalized.equalsIgnoreCase("changeme")
      && !normalized.equalsIgnoreCase("your_calcom_api_key");
  }
}
