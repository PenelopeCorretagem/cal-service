package com.penelopec.calservice.appointment.infrastructure.config;

import com.penelopec.calservice.appointment.application.service.*;
import com.penelopec.calservice.appointment.application.usecase.*;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.adapter.CalComBookingAdapter;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.CalcomProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppointmentConfig {

  @Bean
  public RestClient calBookingRestClient(CalcomProperties prop) {
    return RestClient.builder()
      .baseUrl(prop.api().baseUrl())
      .defaultHeader("Authorization", "Bearer " + prop.api().key())
      .defaultHeader("cal-api-version", prop.api().versionV2())
      .build();
  }

  @Bean
  public CalComBookingGateway calComBookingGateway(RestClient calBookingRestClient) {
    return new CalComBookingAdapter(calBookingRestClient);
  }

  @Bean
  public CreateAppointmentUseCase createAppointmentUseCase(CalComBookingGateway gateway,
                                                           AppointmentRepository repository) {
    return new CreateAppointmentService(gateway, repository);
  }

  @Bean
  public GetAppointmentUseCase getAppointmentUseCase(AppointmentRepository repository) {
    return new GetAppointmentService(repository);
  }

  @Bean
  public ListAppointmentsUseCase listAppointmentsUseCase(AppointmentRepository repository) {
    return new ListAppointmentsService(repository);
  }

  @Bean
  public ChangeAppointmentUseCase rescheduleAppointmentUseCase(CalComBookingGateway gateway,
                                                               AppointmentRepository repository) {
    return new RescheduleAppointmentService(gateway, repository);
  }

  @Bean
  public CancelAppointmentUseCase cancelAppointmentUseCase(CalComBookingGateway gateway,
                                                           AppointmentRepository repository) {
    return new CancelAppointmentService(gateway, repository);
  }

  @Bean
  public ConfirmAppointmentUseCase confirmAppointmentUseCase(AppointmentRepository repository) {
    return new ConfirmAppointmentService(repository);
  }

  @Bean
  public ConcludeAppointmentUseCase concludeAppointmentUseCase(AppointmentRepository repository) {
    return new ConcludeAppointmentService(repository);
  }

  @Bean
  public DeleteAppointmentUseCase deleteAppointmentUseCase(CalComBookingGateway gateway,
                                                           AppointmentRepository repository) {
    return new DeleteAppointmentService(gateway, repository);
  }
}
