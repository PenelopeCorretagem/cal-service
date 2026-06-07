package com.penelopec.calservice.appointment.infrastructure.web.monolith.adapter;

import com.penelopec.calservice.appointment.domain.error.AppointmentError;
import com.penelopec.calservice.appointment.domain.gateway.EstateData;
import com.penelopec.calservice.appointment.domain.gateway.EstateEnrichmentGateway;
import com.penelopec.calservice.shared.cache.CacheNames;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.MonolithProperties;
import org.springframework.cache.annotation.Cacheable;
import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;
import com.penelopec.calservice.shared.error.core.GatewayException;
import com.penelopec.calservice.shared.http.exception.RemoteServiceException;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class AppointmentEstateEnrichmentAdapter implements EstateEnrichmentGateway {

    private static final ParameterizedTypeReference<List<AdvertisementResponse>>
        LIST_TYPE = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final MonolithProperties properties;
    private final RestExecutor restExecutor;

    public AppointmentEstateEnrichmentAdapter(RestClient monolithRestClient,
                                              MonolithProperties properties,
                                              RestExecutor restExecutor) {
        this.restClient = monolithRestClient;
        this.properties = properties;
        this.restExecutor = restExecutor;
    }

    @Override
    @Cacheable(value = CacheNames.ESTATES, unless = "#result.isEmpty()")
    public List<EstateData> fetchAll() {
        try {
            List<AdvertisementResponse> responses = restExecutor.executeOrNull("MONOLITH", () ->
                restClient.get()
                    .uri(properties.api().advertisementsPath())
                    .retrieve()
                    .body(LIST_TYPE)
            );

            if (responses == null) {
                return List.of();
            }

            return responses.stream()
                .filter(r -> r.active() && r.estate() != null)
                .map(r -> new EstateData(
                    r.estate().id(),
                    r.estate().title(),
                    r.estate().type()
                ))
                .toList();

        } catch (RemoteServiceException e) {
            throw new GatewayException(AppointmentError.ESTATE_ENRICHMENT_FAILED, e);
        }
    }
}
