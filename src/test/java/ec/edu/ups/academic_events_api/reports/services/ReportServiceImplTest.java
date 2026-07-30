package ec.edu.ups.academic_events_api.reports.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ec.edu.ups.academic_events_api.core.exceptions.domain.BadRequestException;
import ec.edu.ups.academic_events_api.core.exceptions.domain.ForbiddenException;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.reports.utils.CertificatePdfGenerator;
import ec.edu.ups.academic_events_api.reports.utils.ExcelGenerator;
import ec.edu.ups.academic_events_api.reports.utils.PdfGenerator;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private PdfGenerator pdfGenerator;

    @Mock
    private ExcelGenerator excelGenerator;

    @Mock
    private CertificatePdfGenerator certificatePdfGenerator;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(
                eventRepository,
                registrationRepository,
                pdfGenerator,
                excelGenerator,
                certificatePdfGenerator
        );
    }

    @Test
    @DisplayName(
            "Debe generar el reporte PDF para el organizador propietario"
    )
    void generateRegistrationsPdfShouldAllowEventOwner() {
        Long eventId = 1L;
        Long organizerId = 2L;

        UserEntity organizer =
                org.mockito.Mockito.mock(UserEntity.class);

        EventEntity event =
                org.mockito.Mockito.mock(EventEntity.class);

        RegistrationEntity registration =
                org.mockito.Mockito.mock(
                        RegistrationEntity.class
                );

        List<RegistrationEntity> registrations =
                List.of(registration);

        byte[] expectedPdf = {
                1, 2, 3, 4
        };

        when(organizer.getId())
                .thenReturn(organizerId);

        when(event.getOrganizer())
                .thenReturn(organizer);

        when(
                eventRepository.findByIdAndDeletedFalse(
                        eventId
                )
        ).thenReturn(Optional.of(event));

        when(
                registrationRepository
                        .findByEventIdOrderByRegisteredAtAsc(
                                eventId
                        )
        ).thenReturn(registrations);

        when(
                pdfGenerator.generateRegistrationsReport(
                        event,
                        registrations
                )
        ).thenReturn(expectedPdf);

        byte[] result =
                reportService.generateRegistrationsPdf(
                        eventId,
                        organizerId,
                        false
                );

        assertArrayEquals(expectedPdf, result);

        verify(eventRepository)
                .findByIdAndDeletedFalse(eventId);

        verify(registrationRepository)
                .findByEventIdOrderByRegisteredAtAsc(
                        eventId
                );

        verify(pdfGenerator)
                .generateRegistrationsReport(
                        event,
                        registrations
                );
    }

    @Test
    @DisplayName(
            "Debe permitir al administrador generar el reporte Excel"
    )
    void generateRegistrationsExcelShouldAllowAdmin() {
        Long eventId = 2L;
        Long adminId = 1L;

        EventEntity event =
                org.mockito.Mockito.mock(EventEntity.class);

        RegistrationEntity registration =
                org.mockito.Mockito.mock(
                        RegistrationEntity.class
                );

        List<RegistrationEntity> registrations =
                List.of(registration);

        byte[] expectedExcel = {
                10, 20, 30
        };

        when(
                eventRepository.findByIdAndDeletedFalse(
                        eventId
                )
        ).thenReturn(Optional.of(event));

        when(
                registrationRepository
                        .findByEventIdOrderByRegisteredAtAsc(
                                eventId
                        )
        ).thenReturn(registrations);

        when(
                excelGenerator.generateRegistrationsReport(
                        event,
                        registrations
                )
        ).thenReturn(expectedExcel);

        byte[] result =
                reportService.generateRegistrationsExcel(
                        eventId,
                        adminId,
                        true
                );

        assertArrayEquals(expectedExcel, result);

        verify(eventRepository)
                .findByIdAndDeletedFalse(eventId);

        verify(registrationRepository)
                .findByEventIdOrderByRegisteredAtAsc(
                        eventId
                );

        verify(excelGenerator)
                .generateRegistrationsReport(
                        event,
                        registrations
                );
    }

    @Test
    @DisplayName(
            "Debe impedir generar reportes de un evento ajeno"
    )
    void generateRegistrationsPdfShouldRejectForeignOrganizer() {
        Long eventId = 2L;
        Long eventOwnerId = 3L;
        Long authenticatedUserId = 2L;

        UserEntity organizer =
                org.mockito.Mockito.mock(UserEntity.class);

        EventEntity event =
                org.mockito.Mockito.mock(EventEntity.class);

        when(organizer.getId())
                .thenReturn(eventOwnerId);

        when(event.getOrganizer())
                .thenReturn(organizer);

        when(
                eventRepository.findByIdAndDeletedFalse(
                        eventId
                )
        ).thenReturn(Optional.of(event));

        assertThrows(
                ForbiddenException.class,
                () -> reportService
                        .generateRegistrationsPdf(
                                eventId,
                                authenticatedUserId,
                                false
                        )
        );

        verify(
                registrationRepository,
                never()
        ).findByEventIdOrderByRegisteredAtAsc(eventId);

        verify(
                pdfGenerator,
                never()
        ).generateRegistrationsReport(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.anyList()
        );
    }

    @Test
    @DisplayName(
            "Debe generar el certificado para el participante propietario"
    )
    void generateCertificateShouldAllowParticipantOwner() {
        Long registrationId = 1L;
        Long participantId = 5L;

        UserEntity participant =
                org.mockito.Mockito.mock(UserEntity.class);

        RegistrationEntity registration =
                org.mockito.Mockito.mock(
                        RegistrationEntity.class
                );

        byte[] expectedCertificate = {
                50, 60, 70
        };

        when(participant.getId())
                .thenReturn(participantId);

        when(registration.getParticipant())
                .thenReturn(participant);

        when(registration.getStatus())
                .thenReturn("CONFIRMED");

        when(
                registrationRepository
                        .findByIdWithParticipantAndEvent(
                                registrationId
                        )
        ).thenReturn(Optional.of(registration));

        when(
                certificatePdfGenerator
                        .generateCertificate(registration)
        ).thenReturn(expectedCertificate);

        byte[] result =
                reportService.generateCertificatePdf(
                        registrationId,
                        participantId
                );

        assertArrayEquals(
                expectedCertificate,
                result
        );

        verify(certificatePdfGenerator)
                .generateCertificate(registration);
    }

    @Test
    @DisplayName(
            "Debe impedir generar certificado de otro participante"
    )
    void generateCertificateShouldRejectForeignParticipant() {
        Long registrationId = 2L;
        Long participantOwnerId = 6L;
        Long authenticatedUserId = 5L;

        UserEntity participant =
                org.mockito.Mockito.mock(UserEntity.class);

        RegistrationEntity registration =
                org.mockito.Mockito.mock(
                        RegistrationEntity.class
                );

        when(participant.getId())
                .thenReturn(participantOwnerId);

        when(registration.getParticipant())
                .thenReturn(participant);

        when(
                registrationRepository
                        .findByIdWithParticipantAndEvent(
                                registrationId
                        )
        ).thenReturn(Optional.of(registration));

        assertThrows(
                ForbiddenException.class,
                () -> reportService.generateCertificatePdf(
                        registrationId,
                        authenticatedUserId
                )
        );

        verify(
                certificatePdfGenerator,
                never()
        ).generateCertificate(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @DisplayName(
            "Debe impedir generar certificado para inscripción no confirmada"
    )
    void generateCertificateShouldRejectPendingRegistration() {
        Long registrationId = 8L;
        Long participantId = 9L;

        UserEntity participant =
                org.mockito.Mockito.mock(UserEntity.class);

        RegistrationEntity registration =
                org.mockito.Mockito.mock(
                        RegistrationEntity.class
                );

        when(participant.getId())
                .thenReturn(participantId);

        when(registration.getParticipant())
                .thenReturn(participant);

        when(registration.getStatus())
                .thenReturn("PENDING");

        when(
                registrationRepository
                        .findByIdWithParticipantAndEvent(
                                registrationId
                        )
        ).thenReturn(Optional.of(registration));

        assertThrows(
                BadRequestException.class,
                () -> reportService.generateCertificatePdf(
                        registrationId,
                        participantId
                )
        );

        verify(
                certificatePdfGenerator,
                never()
        ).generateCertificate(
                org.mockito.ArgumentMatchers.any()
        );
    }
}