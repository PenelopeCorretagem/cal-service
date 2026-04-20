package com.penelopec.calservice.eventtype.infrastructure.config;

import com.penelopec.calservice.eventtype.infrastructure.config.properties.RabbitMQProperties;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

  private static final String DLX_NAME = "cal-service.dlx";
  private static final String DLQ_ROUTING_KEY = "cal-service.estate-changed.dlq";
  private static final String DLQ_NAME = "cal-service.estate-changed.dlq";
  private static final String ROUTING_KEY_ESTATE_CHANGED = "estate.changed";

  private final RabbitMQProperties properties;

  public RabbitMQConfig(RabbitMQProperties properties) {
    this.properties = properties;
  }

  @Bean
  public TopicExchange estateExchange() {
    return new TopicExchange(properties.exchanges().estate(), true, false);
  }

  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange(DLX_NAME, true, false);
  }

  @Bean
  public Queue estateChangedQueue() {
    return QueueBuilder.durable(properties.queues().estateChanged())
      .withArgument("x-dead-letter-exchange", DLX_NAME)
      .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
      .build();
  }

  @Bean
  public Queue estateChangedDlq() {
    return QueueBuilder.durable(DLQ_NAME).build();
  }

  @Bean
  public Binding estateChangedBinding() {
    return BindingBuilder
      .bind(estateChangedQueue())
      .to(estateExchange())
      .with(ROUTING_KEY_ESTATE_CHANGED);
  }

  @Bean
  public Binding dlqBinding() {
    return BindingBuilder
      .bind(estateChangedDlq())
      .to(deadLetterExchange())
      .with(DLQ_ROUTING_KEY);
  }

  @Bean
  public JacksonJsonMessageConverter messageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
    ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(messageConverter());
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    factory.setDefaultRequeueRejected(false);
    return factory;
  }
}
