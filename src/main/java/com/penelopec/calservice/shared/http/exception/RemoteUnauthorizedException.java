package com.penelopec.calservice.shared.http.exception;

public class RemoteUnauthorizedException extends RemoteServiceException {
  public RemoteUnauthorizedException(String system) {
    super(system, 401, "Não autorizado");
  }
}
