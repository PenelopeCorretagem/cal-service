package com.penelopec.calservice.shared.http.exception;

public class RemoteNotFoundException extends RemoteServiceException {
  public RemoteNotFoundException(String system, String resource) {
    super(system, 404, "Recurso não encontrado: " + resource);
  }
}
