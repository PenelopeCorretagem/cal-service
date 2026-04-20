package com.penelopec.calservice.auth.domain.gateway;

import com.penelopec.calservice.auth.application.output.LoginOutput;
import com.penelopec.calservice.auth.application.output.ValidateTokenOutput;

public interface AuthGateway {
  LoginOutput login(String email, String password);
  ValidateTokenOutput validateToken(String token);
}
