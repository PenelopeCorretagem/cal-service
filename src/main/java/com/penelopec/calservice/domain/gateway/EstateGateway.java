package com.penelopec.calservice.domain.gateway;

import java.util.List;

public interface EstateGateway {

    List<EstateData> fetchAllEstates();
}
