package com.penelopec.calservice.eventtype.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitMQProperties(Queues queues, Exchanges exchanges) {

  public record Queues(String estateChanged) {
  }

  public record Exchanges(String estate) {
  }
}
