package com.penelopec.calservice.eventtype.infrastructure.messaging;

import com.penelopec.calservice.eventtype.application.command.CreateEventTypeCommand;
import com.penelopec.calservice.eventtype.application.command.HandleEstateChangedCommand;
import com.penelopec.calservice.eventtype.application.port.in.CreateEventTypeUseCase;
import com.penelopec.calservice.eventtype.application.port.in.HandleEstateChangedUseCase;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateChangedConsumer;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateChangedMessage;
import com.penelopec.calservice.eventtype.infrastructure.messaging.EstateStatus;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EstateChangedConsumerTest {

  @Mock
  private HandleEstateChangedUseCase handleEstateChangedUseCase;

  @Mock
  private CreateEventTypeUseCase createEventTypeUseCase;

  @Mock
  private Channel channel;

  private EstateChangedConsumer consumer;

  @BeforeEach
  void setUp() {
    consumer = new EstateChangedConsumer(handleEstateChangedUseCase, createEventTypeUseCase);
  }

  @Nested
  @DisplayName("consume")
  class Consume {

    @Test
    @DisplayName("Deve delegar ao handleEstateChangedUseCase com hide=true quando status INACTIVE")
    void shouldDelegateWithHideTrue_whenStatusInactive() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(
          42L, 1L, "Title", "Desc", "slug", EstateChangedMessage.ACTION_UPDATED, EstateStatus.INACTIVE, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      ArgumentCaptor<HandleEstateChangedCommand> captor = ArgumentCaptor.forClass(HandleEstateChangedCommand.class);
      verify(handleEstateChangedUseCase).execute(captor.capture());
      verifyNoInteractions(createEventTypeUseCase);
      verify(channel).basicAck(1L, false);
      assertThat(captor.getValue().estateId()).isEqualTo(42L);
      assertThat(captor.getValue().hide()).isTrue();
    }

    @Test
    @DisplayName("Deve delegar ao handleEstateChangedUseCase com hide=false quando status ACTIVE")
    void shouldDelegateWithHideFalse_whenStatusActive() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(
          10L, 2L, "Title", "Desc", "slug", EstateChangedMessage.ACTION_UPDATED, EstateStatus.ACTIVE, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      ArgumentCaptor<HandleEstateChangedCommand> captor = ArgumentCaptor.forClass(HandleEstateChangedCommand.class);
      verify(handleEstateChangedUseCase).execute(captor.capture());
      verifyNoInteractions(createEventTypeUseCase);
      verify(channel).basicAck(1L, false);
      assertThat(captor.getValue().estateId()).isEqualTo(10L);
      assertThat(captor.getValue().hide()).isFalse();
    }

    @Test
    @DisplayName("Deve delegar ao createEventTypeUseCase quando action for CREATED")
    void shouldDelegateToCreate_whenActionCreated() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(
          10L, 2L, "Title", "Desc", "slug", EstateChangedMessage.ACTION_CREATED, EstateStatus.ACTIVE, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      ArgumentCaptor<CreateEventTypeCommand> captor = ArgumentCaptor.forClass(CreateEventTypeCommand.class);
      verify(createEventTypeUseCase).execute(captor.capture());
      verifyNoInteractions(handleEstateChangedUseCase);
      verify(channel).basicAck(1L, false);
      assertThat(captor.getValue().estateId()).isEqualTo(10L);
      assertThat(captor.getValue().title()).isEqualTo("Title");
      assertThat(captor.getValue().description()).isEqualTo("Desc");
      assertThat(captor.getValue().hidden()).isFalse();
    }

    @Test
    @DisplayName("Deve descartar mensagem sem chamar use case quando newStatus for nulo")
    void shouldDiscard_whenNewStatusIsNull() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(
          5L, 3L, "Title", "Desc", "slug", EstateChangedMessage.ACTION_UPDATED, null, Instant.now());

      // When
      consumer.consume(message, channel, 1L);

      // Then
      verifyNoInteractions(handleEstateChangedUseCase);
      verifyNoInteractions(createEventTypeUseCase);
      verify(channel).basicAck(1L, false);
    }

    @Test
    @DisplayName("Deve propagar exceção e não confirmar mensagem quando useCase lança exceção")
    void shouldPropagateException_whenUseCaseThrowsException() throws Exception {
      // Given
      EstateChangedMessage message = new EstateChangedMessage(
          99L, 4L, "Title", "Desc", "slug", EstateChangedMessage.ACTION_UPDATED, EstateStatus.ACTIVE, Instant.now());
      doThrow(new RuntimeException("erro simulado")).when(handleEstateChangedUseCase).execute(any());

      // When
      assertThatThrownBy(() -> consumer.consume(message, channel, 2L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("erro simulado");

      // Then
      verify(channel, never()).basicAck(2L, false);
      verify(channel, never()).basicNack(2L, false, false);
    }
  }
}

