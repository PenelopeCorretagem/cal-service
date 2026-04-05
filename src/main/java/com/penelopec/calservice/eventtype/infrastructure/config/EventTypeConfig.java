package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.eventtype.application.port.in.*;
import com.penelopec.calservice.eventtype.application.service.*;
import com.penelopec.calservice.eventtype.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.eventtype.domain.gateway.EstateGateway;
import com.penelopec.calservice.eventtype.domain.repository.EventTypeRepository;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.CalcomProperties;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({CalcomProperties.class, CorsProperties.class})
public class EventTypeConfig {

  @Bean
  public CreateEventTypeUseCase createEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                       EventTypeRepository repository) {
    return new CreateEventTypeService(calComGateway, repository);
  }

  @Bean
  public ChangeEventTypeUseCase updateEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                       EventTypeRepository repository) {
    return new ChangeEventTypeService(calComGateway, repository);
  }

  @Bean
  public DeleteEventTypeUseCase deleteEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                       EventTypeRepository repository) {
    return new DeleteEventTypeService(calComGateway, repository);
  }

  @Bean
  public GetEventTypeUseCase getEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                 EventTypeRepository repository) {
    return new GetEventTypeService(calComGateway, repository);
  }

  @Bean
  public ListEventTypesUseCase listEventTypesUseCase(CalComEventTypeGateway calComGateway) {
    return new ListEventTypesService(calComGateway);
  }

  @Bean
  public ToggleEventTypeVisibilityUseCase toggleEventTypeVisibilityUseCase(CalComEventTypeGateway calComGateway,
                                                                           EventTypeRepository repository) {
    return new ToggleEventTypeVisibilityService(calComGateway, repository);
  }

  @Bean
  public SyncEventTypesUseCase syncEventTypesUseCase(EstateGateway estateGateway,
                                                     CalComEventTypeGateway calComGateway,
                                                     EventTypeRepository repository) {
    return new SyncEventTypesService(estateGateway, calComGateway, repository);
  }

  @Bean
  public HandleEstateChangedUseCase handleEstateChangedUseCase(EventTypeRepository repository,
                                                               CalComEventTypeGateway calComGateway) {
    return new HandleEstateChangedService(repository, calComGateway);
  }
}