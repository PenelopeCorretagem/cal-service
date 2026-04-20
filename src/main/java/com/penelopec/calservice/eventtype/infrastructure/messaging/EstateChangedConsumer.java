package com.penelopec.calservice.eventtype.infrastructure.messaging;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.application.port.in.HandleEstateChangedUseCase;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EstateChangedConsumer {

  private static final Logger log = LoggerFactory.getLogger(EstateChangedConsumer.class);
  private static final int MAX_RETRIES = 3;
  private static final long INITIAL_BACKOFF_MS = 2000;

  private final HandleEstateChangedUseCase useCase;

  public EstateChangedConsumer(HandleEstateChangedUseCase useCase) {
    this.useCase = useCase;
  }

  @RabbitListener(queues = "${rabbitmq.queues.estate-changed}",
      containerFactory = "rabbitListenerContainerFactory")
  public void consume(EstateChangedMessage message,
                      Channel channel,
                      @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
    try {
      if (message.newStatus() == null) {
        log.warn("Mensagem recebida com newStatus nulo para estateId={}. Descartando.", message.estateId());
        channel.basicAck(deliveryTag, false);
        return;
      }

      boolean hide = switch (message.newStatus()) {
        case INACTIVE -> true;
        case ACTIVE -> false;
      };

      executeWithRetry(message.estateId(), new HandleEstateChangedCommand(message.estateId(), hide));
      channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
      log.error("Falha ao processar mensagem para estateId={} após {} tentativas. Enviando para DLQ.",
          message.estateId(), MAX_RETRIES, e);
      channel.basicNack(deliveryTag, false, false);
    }
  }

  private void executeWithRetry(Long estateId, HandleEstateChangedCommand command) {
    for (int attempt = 1; true; attempt++) {
      try {
        useCase.execute(command);
        return;
      } catch (Exception e) {
        if (attempt == MAX_RETRIES) {
          throw e;
        }
        long backoff = INITIAL_BACKOFF_MS * (1L << (attempt - 1));
        log.warn("Tentativa {}/{} falhou para estateId={}. Retentando em {}ms.",
            attempt, MAX_RETRIES, estateId, backoff);
        try {
          Thread.sleep(backoff);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw e;
        }
      }
    }
  }
}
