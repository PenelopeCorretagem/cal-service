package com.penelopec.calservice.user.domain.gateway;

import com.penelopec.calservice.user.application.output.UserSummaryOutput;

import java.util.Optional;

public interface UserGateway {

  Optional<UserSummaryOutput> findById(Long userId);
}
