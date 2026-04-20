package com.penelopec.calservice.eventtype.domain.gateway;

import com.penelopec.calservice.eventtype.domain.valueobject.EstateData;

import java.util.List;

public interface EstateGateway {

  List<EstateData> fetchAllEstates();
}
