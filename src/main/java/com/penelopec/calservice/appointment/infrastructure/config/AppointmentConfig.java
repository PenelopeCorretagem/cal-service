package com.penelopec.calservice.appointment.infrastructure.config;

import com.penelopec.calservice.appointment.application.service.*;
import com.penelopec.calservice.appointment.application.usecase.*;
import com.penelopec.calservice.appointment.application.validator.AppointmentCommandValidator;
import com.penelopec.calservice.appointment.application.validator.CancelAppointmentCommandValidator;
import com.penelopec.calservice.appointment.application.validator.ListAppointmentsQueryValidator;
import com.penelopec.calservice.appointment.application.validator.RescheduleAppointmentCommandValidator;
import com.penelopec.calservice.appointment.domain.gateway.CalComBookingGateway;
import com.penelopec.calservice.appointment.domain.gateway.CalComScheduleGateway;
import com.penelopec.calservice.appointment.domain.repository.AppointmentRepository;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.adapter.CalComBookingAdapter;
import com.penelopec.calservice.appointment.infrastructure.web.calcom.adapter.CalComScheduleAdapter;
import com.penelopec.calservice.eventtype.infrastructure.config.properties.CalcomProperties;
import com.penelopec.calservice.shared.http.config.RestClientBuilderFactory;
import com.penelopec.calservice.shared.http.executor.RestExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppointmentConfig {

  @Bean
  public RestClient calBookingRestClient(CalcomProperties prop, RestClientBuilderFactory restClientBuilderFactory) {
    return restClientBuilderFactory
      .builder(prop.api().baseUrl())
      .defaultHeader("Authorization", "Bearer " + prop.api().key())
      .defaultHeader("cal-api-version", prop.api().versionV2())
      .build();
  }

  @Bean
  public CalComBookingGateway calComBookingGateway(RestClient calBookingRestClient, RestExecutor restExecutor) {
    return new CalComBookingAdapter(calBookingRestClient, restExecutor);
  }

  @Bean
  public CalComScheduleGateway calComScheduleGateway(RestClient calBookingRestClient, RestExecutor restExecutor) {
    return new CalComScheduleAdapter(calBookingRestClient, restExecutor);
  }

  @Bean
  public GetSchedulesUseCase getSchedulesUseCase(CalComScheduleGateway gateway) {
    return new GetSchedulesService(gateway);
  }

  @Bean
  public GetAvailableSlotsUseCase getAvailableSlotsUseCase(CalComScheduleGateway gateway) {
    return new GetAvailableSlotsService(gateway);
  }

  @Bean
  public CreateAppointmentUseCase createAppointmentUseCase(CalComBookingGateway gateway,
                                                           AppointmentRepository repository) {
    return new CreateAppointmentService(gateway, repository, new AppointmentCommandValidator());
  }

  @Bean
  public GetAppointmentUseCase getAppointmentUseCase(AppointmentRepository repository) {
    return new GetAppointmentService(repository);
  }

  @Bean
  public ListAppointmentsUseCase listAppointmentsUseCase(AppointmentRepository repository) {
    return new ListAppointmentsService(repository, new ListAppointmentsQueryValidator());
  }

  @Bean
  public ExportAppointmentsUseCase exportAppointmentsUseCase(AppointmentRepository repository) {
    return new ExportAppointmentsService(repository);
  }

  @Bean
  public ChangeAppointmentUseCase rescheduleAppointmentUseCase(CalComBookingGateway gateway,
                                                               AppointmentRepository repository) {
    return new RescheduleAppointmentService(gateway, repository, new RescheduleAppointmentCommandValidator());
  }

  @Bean
  public CancelAppointmentUseCase cancelAppointmentUseCase(CalComBookingGateway gateway,
                                                           AppointmentRepository repository) {
    return new CancelAppointmentService(gateway, repository, new CancelAppointmentCommandValidator());
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
