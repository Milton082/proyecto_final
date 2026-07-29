package ec.edu.ups.academic_events_api.reports.services;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ec.edu.ups.academic_events_api.core.exceptions.domain.BadRequestException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.ForbiddenException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.NotFoundException;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.reports.dtos.StatisticsResponseDto;
import ec.edu.ups.academic_events_api.reports.utils.CertificatePdfGenerator;
import ec.edu.ups.academic_events_api.reports.utils.ExcelGenerator;
import ec.edu.ups.academic_events_api.reports.utils.PdfGenerator;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_REJECTED = "REJECTED";

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PdfGenerator pdfGenerator;
    private final ExcelGenerator excelGenerator;
    private final CertificatePdfGenerator certificatePdfGenerator;

    public ReportServiceImpl(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            PdfGenerator pdfGenerator,
            ExcelGenerator excelGenerator,
            CertificatePdfGenerator certificatePdfGenerator
    ) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.pdfGenerator = pdfGenerator;
        this.excelGenerator = excelGenerator;
        this.certificatePdfGenerator = certificatePdfGenerator;
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

    @Override
    public byte[] generateRegistrationsPdf(
            Long eventId,
            Long authenticatedUserId,
            boolean admin
    ) {
        EventEntity event =
                findEventAndValidateOwner(
                        eventId,
                        authenticatedUserId,
                        admin
                );

        List<RegistrationEntity> registrations =
                registrationRepository
                        .findByEventIdOrderByRegisteredAtAsc(
                                eventId
                        );

        return pdfGenerator.generateRegistrationsReport(
                event,
                registrations
        );
    }

    @Override
    public byte[] generateRegistrationsExcel(
            Long eventId,
            Long authenticatedUserId,
            boolean admin
    ) {
        EventEntity event =
                findEventAndValidateOwner(
                        eventId,
                        authenticatedUserId,
                        admin
                );

        List<RegistrationEntity> registrations =
                registrationRepository
                        .findByEventIdOrderByRegisteredAtAsc(
                                eventId
                        );

        return excelGenerator.generateRegistrationsReport(
                event,
                registrations
        );
    }

    @Override
    public byte[] generateCertificatePdf(
            Long registrationId,
            Long authenticatedUserId
    ) {
        if (authenticatedUserId == null) {
            throw new ForbiddenException(
                    "No se pudo identificar al usuario autenticado"
            );
        }

        RegistrationEntity registration =
                registrationRepository
                        .findByIdWithParticipantAndEvent(
                                registrationId
                        )
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "REGISTRATION_NOT_FOUND",
                                        "No se encontró la inscripción con id "
                                                + registrationId
                                )
                        );

        Long participantId =
                registration.getParticipant().getId();

        if (!participantId.equals(authenticatedUserId)) {
            throw new ForbiddenException(
                    "CERTIFICATE_ACCESS_DENIED",
                    "Solo el participante propietario puede "
                            + "descargar este certificado"
            );
        }

        if (!STATUS_CONFIRMED.equalsIgnoreCase(
                registration.getStatus()
        )) {
            throw new BadRequestException(
                    "REGISTRATION_NOT_CONFIRMED",
                    "El certificado solo puede generarse para "
                            + "inscripciones confirmadas"
            );
        }

        return certificatePdfGenerator
                .generateCertificate(registration);
    }

    private EventEntity findEventAndValidateOwner(
            Long eventId,
            Long authenticatedUserId,
            boolean admin
    ) {
        if (authenticatedUserId == null) {
            throw new ForbiddenException(
                    "No se pudo identificar al usuario autenticado"
            );
        }

        EventEntity event =
                eventRepository
                        .findByIdAndDeletedFalse(eventId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "EVENT_NOT_FOUND",
                                        "No se encontró el evento con id "
                                                + eventId
                                )
                        );

        if (admin) {
            return event;
        }

        if (event.getOrganizer() == null
                || event.getOrganizer().getId() == null
                || !event.getOrganizer()
                        .getId()
                        .equals(authenticatedUserId)) {

            throw new ForbiddenException(
                    "EVENT_REPORT_ACCESS_DENIED",
                    "Solo el organizador propietario del evento "
                            + "puede descargar este reporte"
            );
        }

        return event;
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
                        || (startDate != null
                        && endDate == null);

        if (onlyOneDateProvided) {
            throw new BadRequestException(
                    "INVALID_DATE_RANGE",
                    "Debe proporcionar startDate y endDate juntos"
            );
        }

        if (startDate != null
                && startDate.isAfter(endDate)) {

            throw new BadRequestException(
                    "INVALID_DATE_RANGE",
                    "startDate no puede ser posterior a endDate"
            );
        }
    }
}