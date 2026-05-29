package com.penelopec.calservice.user.domain.gateway;

import com.penelopec.calservice.user.domain.valueobject.UserSummary;

import java.util.Optional;

public interface UserGateway {

  Optional<UserSummary> findById(Long userId);
}
