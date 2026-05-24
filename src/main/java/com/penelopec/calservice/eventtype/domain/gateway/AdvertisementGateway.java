package com.penelopec.calservice.eventtype.domain.gateway;

import com.penelopec.calservice.eventtype.infrastructure.web.monolith.dto.AdvertisementResponse;

import java.util.List;

public interface AdvertisementGateway {

  List<AdvertisementResponse> fetchAllAdvertisements();
}
