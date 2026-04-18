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

      useCase.execute(new HandleEstateChangedCommand(message.estateId(), hide));
      channel.basicAck(deliveryTag, false);
    } catch (Exception e) {
      log.error("Falha ao processar mensagem para estateId={}. Enviando para DLQ.", message.estateId(), e);
      channel.basicNack(deliveryTag, false, false);
    }
  }
}
