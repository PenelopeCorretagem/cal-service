package com.penelopec.calservice.infrastructure.config;

import com.penelopec.calservice.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.application.port.in.DeleteEventTypeUseCase;
import com.penelopec.calservice.application.port.in.GetEventTypeUseCase;
import com.penelopec.calservice.application.port.in.ListEventTypesUseCase;
import com.penelopec.calservice.application.port.in.SyncEventTypesUseCase;
import com.penelopec.calservice.application.port.in.ToggleEventTypeVisibilityUseCase;
import com.penelopec.calservice.application.port.in.UpdateEventTypeUseCase;
import com.penelopec.calservice.application.service.CreateEventTypeService;
import com.penelopec.calservice.application.service.DeleteEventTypeService;
import com.penelopec.calservice.application.service.GetEventTypeService;
import com.penelopec.calservice.application.service.ListEventTypesService;
import com.penelopec.calservice.application.service.SyncEventTypesService;
import com.penelopec.calservice.application.service.ToggleEventTypeVisibilityService;
import com.penelopec.calservice.application.service.UpdateEventTypeService;
import com.penelopec.calservice.domain.gateway.CalComEventTypeGateway;
import com.penelopec.calservice.domain.gateway.EstateGateway;
import com.penelopec.calservice.domain.repository.EventTypeRepository;
import com.penelopec.calservice.infrastructure.config.properties.CalcomProperties;
import com.penelopec.calservice.infrastructure.config.properties.CorsProperties;
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
    public UpdateEventTypeUseCase updateEventTypeUseCase(CalComEventTypeGateway calComGateway,
                                                         EventTypeRepository repository) {
        return new UpdateEventTypeService(calComGateway, repository);
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
}