package ec.edu.ups.academic_events_api.registrations.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.dtos.CreateRegistrationDto;
import ec.edu.ups.academic_events_api.registrations.dtos.RegistrationResponseDto;
import ec.edu.ups.academic_events_api.registrations.dtos.UpdateRegistrationStatusDto;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.registrations.mappers.RegistrationMapper;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        registrationService = new RegistrationServiceImpl(
                registrationRepository,
                eventRepository,
                userRepository
        );
    }

    @Test
    @DisplayName(
            "Debe impedir una inscripción duplicada"
    )
    void createShouldRejectDuplicatedRegistration() {
        CreateRegistrationDto dto =
                new CreateRegistrationDto();

        dto.setEventId(1L);
        dto.setParticipantId(5L);

        when(
                registrationRepository
                        .existsByEventIdAndParticipantId(
                                1L,
                                5L
                        )
        ).thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> registrationService.create(dto)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        assertEquals(
                "El usuario ya está registrado en este evento",
                exception.getReason()
        );

        verify(eventRepository, never())
                .findByIdAndDeletedFalse(
                        org.mockito.ArgumentMatchers.anyLong()
                );

        verify(registrationRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @DisplayName(
            "Debe impedir una inscripción cuando no existen cupos"
    )
    void createShouldRejectEventWithoutCapacity() {
        CreateRegistrationDto dto =
                new CreateRegistrationDto();

        dto.setEventId(1L);
        dto.setParticipantId(5L);

        EventEntity event = new EventEntity();
        event.setStatus("PUBLISHED");
        event.setEndDate(
                LocalDateTime.now().plusDays(10)
        );
        event.setCapacity(30);
        event.setAvailableCapacity(0);

        when(
                registrationRepository
                        .existsByEventIdAndParticipantId(
                                1L,
                                5L
                        )
        ).thenReturn(false);

        when(eventRepository.findByIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(event));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> registrationService.create(dto)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        assertEquals(
                "El evento no tiene cupos disponibles",
                exception.getReason()
        );

        verify(userRepository, never()).findById(
                org.mockito.ArgumentMatchers.anyLong()
        );

        verify(registrationRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @DisplayName(
            "Debe impedir una inscripción en un evento finalizado"
    )
    void createShouldRejectFinishedEvent() {
        CreateRegistrationDto dto =
                new CreateRegistrationDto();

        dto.setEventId(6L);
        dto.setParticipantId(10L);

        EventEntity event = new EventEntity();
        event.setStatus("PUBLISHED");
        event.setEndDate(
                LocalDateTime.now().minusDays(1)
        );
        event.setCapacity(50);
        event.setAvailableCapacity(10);

        when(
                registrationRepository
                        .existsByEventIdAndParticipantId(
                                6L,
                                10L
                        )
        ).thenReturn(false);

        when(eventRepository.findByIdAndDeletedFalse(6L))
                .thenReturn(Optional.of(event));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> registrationService.create(dto)
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        assertEquals(
                "No se puede registrar en un evento finalizado",
                exception.getReason()
        );

        verify(userRepository, never()).findById(
                org.mockito.ArgumentMatchers.anyLong()
        );

        verify(registrationRepository, never()).save(
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    @DisplayName(
            "Debe disminuir el cupo al confirmar una inscripción"
    )
    void updateStatusShouldDecreaseCapacityWhenConfirmed() {
        Long registrationId = 8L;

        EventEntity event = new EventEntity();
        event.setStatus("PUBLISHED");
        event.setCapacity(40);
        event.setAvailableCapacity(5);
        event.setEndDate(
                LocalDateTime.now().plusDays(10)
        );

        RegistrationEntity registration =
                new RegistrationEntity();

        registration.setEvent(event);
        registration.setStatus("PENDING");

        UpdateRegistrationStatusDto dto =
                new UpdateRegistrationStatusDto();

        dto.setStatus("CONFIRMED");

        when(registrationRepository.findById(registrationId))
                .thenReturn(Optional.of(registration));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(registrationRepository.save(registration))
                .thenReturn(registration);

        RegistrationResponseDto expectedResponse =
                new RegistrationResponseDto();

        try (
                MockedStatic<RegistrationMapper> mapper =
                        mockStatic(RegistrationMapper.class)
        ) {
            mapper.when(
                    () -> RegistrationMapper.toResponseDto(
                            registration
                    )
            ).thenReturn(expectedResponse);

            RegistrationResponseDto result =
                    registrationService.updateStatus(
                            registrationId,
                            dto
                    );

            assertEquals(expectedResponse, result);
        }

        assertEquals(
                4,
                event.getAvailableCapacity()
        );

        assertEquals(
                "CONFIRMED",
                registration.getStatus()
        );

        verify(eventRepository).save(event);
        verify(registrationRepository).save(registration);
    }

    @Test
    @DisplayName(
            "Debe restaurar el cupo al cancelar una inscripción confirmada"
    )
    void updateStatusShouldRestoreCapacityWhenCancelled() {
        Long registrationId = 8L;

        EventEntity event = new EventEntity();
        event.setCapacity(40);
        event.setAvailableCapacity(4);

        RegistrationEntity registration =
                new RegistrationEntity();

        registration.setEvent(event);
        registration.setStatus("CONFIRMED");

        UpdateRegistrationStatusDto dto =
                new UpdateRegistrationStatusDto();

        dto.setStatus("CANCELLED");

        when(registrationRepository.findById(registrationId))
                .thenReturn(Optional.of(registration));

        when(eventRepository.save(event))
                .thenReturn(event);

        when(registrationRepository.save(registration))
                .thenReturn(registration);

        try (
                MockedStatic<RegistrationMapper> mapper =
                        mockStatic(RegistrationMapper.class)
        ) {
            mapper.when(
                    () -> RegistrationMapper.toResponseDto(
                            registration
                    )
            ).thenReturn(new RegistrationResponseDto());

            registrationService.updateStatus(
                    registrationId,
                    dto
            );
        }

        assertEquals(
                5,
                event.getAvailableCapacity()
        );

        assertEquals(
                "CANCELLED",
                registration.getStatus()
        );

        verify(eventRepository).save(event);
        verify(registrationRepository).save(registration);
    }
}