package com.penelopec.calservice.eventtype.domain.gateway;

import java.util.List;

public interface EstateGateway {

  List<EstateData> fetchAllEstates();
}
