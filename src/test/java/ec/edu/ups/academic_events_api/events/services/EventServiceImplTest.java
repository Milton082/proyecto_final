package ec.edu.ups.academic_events_api.events.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import ec.edu.ups.academic_events_api.categories.repositories.CategoryRepository;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(
                eventRepository,
                categoryRepository,
                userRepository,
                registrationRepository
        );
    }

    @Test
    @DisplayName(
            "Debe impedir que un organizador modifique un evento ajeno"
    )
    void updateShouldRejectForeignOrganizer() {
        Long eventId = 2L;
        Long eventOwnerId = 3L;
        Long authenticatedUserId = 2L;

        UserEntity organizer = mock(UserEntity.class);

        when(organizer.getId())
                .thenReturn(eventOwnerId);

        EventEntity event = new EventEntity();
        event.setOrganizer(organizer);

        UpdateEventDto dto = new UpdateEventDto();

        when(eventRepository.findByIdAndDeletedFalse(eventId))
                .thenReturn(Optional.of(event));

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> eventService.update(
                                eventId,
                                dto,
                                authenticatedUserId,
                                false
                        )
                );

        assertEquals(
                HttpStatus.FORBIDDEN,
                exception.getStatusCode()
        );

        assertEquals(
                "Solo el organizador propietario puede modificar este evento",
                exception.getReason()
        );

        verify(eventRepository)
                .findByIdAndDeletedFalse(eventId);

        verify(eventRepository, never())
                .save(event);

        verify(categoryRepository, never())
                .findById(anyLong());
    }

    @Test
    @DisplayName(
            "Debe impedir eliminar un evento con inscripciones"
    )
    void deleteShouldRejectEventWithRegistrations() {
        Long eventId = 1L;
        Long organizerId = 2L;

        UserEntity organizer = mock(UserEntity.class);

        when(organizer.getId())
                .thenReturn(organizerId);

        EventEntity event = new EventEntity();
        event.setOrganizer(organizer);

        when(eventRepository.findByIdAndDeletedFalse(eventId))
                .thenReturn(Optional.of(event));

        when(registrationRepository.existsByEventId(eventId))
                .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> eventService.delete(
                                eventId,
                                organizerId,
                                false
                        )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        assertEquals(
                "No se puede eliminar un evento que tiene inscripciones",
                exception.getReason()
        );

        verify(registrationRepository)
                .existsByEventId(eventId);

        verify(eventRepository, never())
                .save(event);
    }

    @Test
    @DisplayName(
            "El administrador tampoco debe eliminar un evento con inscripciones"
    )
    void deleteShouldRejectRegisteredEventForAdmin() {
        Long eventId = 1L;

        EventEntity event = new EventEntity();

        when(eventRepository.findByIdAndDeletedFalse(eventId))
                .thenReturn(Optional.of(event));

        when(registrationRepository.existsByEventId(eventId))
                .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> eventService.delete(
                                eventId,
                                1L,
                                true
                        )
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(registrationRepository)
                .existsByEventId(eventId);

        verify(eventRepository, never())
                .save(event);
    }
}