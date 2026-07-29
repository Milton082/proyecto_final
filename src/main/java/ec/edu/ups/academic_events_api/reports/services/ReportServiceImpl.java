package ec.edu.ups.academic_events_api.reports.services;

import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.reports.dtos.StatisticsResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    public ReportServiceImpl(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    public StatisticsResponseDto getStatistics(
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        validateDateRange(startDate, endDate);

        StatisticsResponseDto response =
                new StatisticsResponseDto();

        response.setTotalEvents(
                eventRepository.countByDeletedFalse()
        );

        if (startDate == null) {
            loadGeneralStatistics(response);
        } else {
            loadStatisticsByDateRange(
                    response,
                    startDate,
                    endDate
            );
        }

        return response;
    }

    private void loadGeneralStatistics(
            StatisticsResponseDto response
    ) {
        response.setTotalRegistrations(
                registrationRepository.count()
        );

        response.setConfirmedRegistrations(
                registrationRepository.countByStatus(
                        STATUS_CONFIRMED
                )
        );

        response.setPendingRegistrations(
                registrationRepository.countByStatus(
                        STATUS_PENDING
                )
        );

        response.setCancelledRegistrations(
                registrationRepository.countByStatus(
                        STATUS_CANCELLED
                )
        );

        response.setRejectedRegistrations(
                registrationRepository.countByStatus(
                        STATUS_REJECTED
                )
        );
    }

    private void loadStatisticsByDateRange(
            StatisticsResponseDto response,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        response.setTotalRegistrations(
                registrationRepository
                        .countByRegisteredAtBetween(
                                startDate,
                                endDate
                        )
        );

        response.setConfirmedRegistrations(
                registrationRepository
                        .countByStatusAndRegisteredAtBetween(
                                STATUS_CONFIRMED,
                                startDate,
                                endDate
                        )
        );

        response.setPendingRegistrations(
                registrationRepository
                        .countByStatusAndRegisteredAtBetween(
                                STATUS_PENDING,
                                startDate,
                                endDate
                        )
        );

        response.setCancelledRegistrations(
                registrationRepository
                        .countByStatusAndRegisteredAtBetween(
                                STATUS_CANCELLED,
                                startDate,
                                endDate
                        )
        );

        response.setRejectedRegistrations(
                registrationRepository
                        .countByStatusAndRegisteredAtBetween(
                                STATUS_REJECTED,
                                startDate,
                                endDate
                        )
        );
    }

    private void validateDateRange(
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        boolean onlyOneDateProvided =
                (startDate == null && endDate != null)
                        || (startDate != null && endDate == null);

        if (onlyOneDateProvided) {
            throw new IllegalArgumentException(
                    "Debe proporcionar startDate y endDate juntos"
            );
        }

        if (startDate != null
                && startDate.isAfter(endDate)) {

            throw new IllegalArgumentException(
                    "startDate no puede ser posterior a endDate"
            );
        }
    }
}