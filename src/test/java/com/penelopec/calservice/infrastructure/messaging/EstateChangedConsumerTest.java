package com.penelopec.calservice.infrastructure.messaging;

import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.application.port.in.HandleEstateChangedUseCase;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateChangedConsumer;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateChangedMessage;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateStatus;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EstateChangedConsumerTest {

  @Mock
  private HandleEstateChangedUseCase useCase;

  @Mock
  private Channel channel;

  @InjectMocks
  private EstateChangedConsumer consumer;

  @Nested
  @DisplayName("consume")
  class Consume {

    @Test
    @DisplayName("Deve delegar ao use case com hide=true quando status INACTIVE")
    void shouldDelegateWithHideTrue_whenStatusInactive() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(42L, EstateStatus.INACTIVE, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      ArgumentCaptor<HandleEstateChangedCommand> captor = ArgumentCaptor.forClass(HandleEstateChangedCommand.class);
      verify(useCase).execute(captor.capture());
      verify(channel).basicAck(1L, false);
      assertThat(captor.getValue().estateId()).isEqualTo(42L);
      assertThat(captor.getValue().hide()).isTrue();
    }

    @Test
    @DisplayName("Deve delegar ao use case com hide=false quando status ACTIVE")
    void shouldDelegateWithHideFalse_whenStatusActive() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(10L, EstateStatus.ACTIVE, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      ArgumentCaptor<HandleEstateChangedCommand> captor = ArgumentCaptor.forClass(HandleEstateChangedCommand.class);
      verify(useCase).execute(captor.capture());
      verify(channel).basicAck(1L, false);
      assertThat(captor.getValue().estateId()).isEqualTo(10L);
      assertThat(captor.getValue().hide()).isFalse();
    }

    @Test
    @DisplayName("Deve descartar mensagem sem chamar use case quando newStatus for nulo")
    void shouldDiscard_whenNewStatusIsNull() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(5L, null, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      verifyNoInteractions(useCase);
      verify(channel).basicAck(1L, false);
    }
  }
}
