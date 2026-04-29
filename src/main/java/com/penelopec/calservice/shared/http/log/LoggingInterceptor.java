package com.penelopec.calservice.shared.http.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

  private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                      ClientHttpRequestExecution execution) throws IOException {
    String method = request.getMethod() == null ? "UNKNOWN" : request.getMethod().name();
    String sanitizedUri = sanitize(request.getURI());
    long startedAtNanos = System.nanoTime();

    if (log.isDebugEnabled()) {
      log.debug("OUTBOUND {} {}", method, sanitizedUri);
    }

    ClientHttpResponse response = execution.execute(request, body);

    if (log.isDebugEnabled()) {
      long elapsedMs = (System.nanoTime() - startedAtNanos) / 1_000_000;
      log.debug("RESPONSE {} {} {} ({} ms)", response.getStatusCode().value(), method, sanitizedUri, elapsedMs);
    }

    return response;
  }

  private String sanitize(URI uri) {
    if (uri == null) {
      return "unknown";
    }

    if (uri.getRawQuery() == null || uri.getRawQuery().isBlank()) {
      return uri.toString();
    }

    String[] queryParams = uri.getRawQuery().split("&");
    for (int i = 0; i < queryParams.length; i++) {
      String[] pair = queryParams[i].split("=", 2);
      String key = pair[0];
      if (isSensitiveKey(key)) {
        queryParams[i] = key + "=***";
      }
    }

    String maskedQuery = String.join("&", queryParams);
    return UriComponentsBuilder.fromUri(uri)
      .replaceQuery(maskedQuery)
      .build(true)
      .toUriString();
  }

  private boolean isSensitiveKey(String key) {
    String normalized = key == null ? "" : key.toLowerCase(Locale.ROOT);
    return normalized.contains("token")
      || normalized.contains("secret")
      || normalized.contains("password")
      || normalized.contains("api_key")
      || normalized.contains("apikey")
      || normalized.contains("authorization");
  }
}