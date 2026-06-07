package com.penelopec.calservice.appointment.domain.gateway;

import java.util.Map;
import java.util.Set;

public interface UserDirectoryGateway {

  Map<Long, String> fetchUserNames(Set<Long> userIds);
}
