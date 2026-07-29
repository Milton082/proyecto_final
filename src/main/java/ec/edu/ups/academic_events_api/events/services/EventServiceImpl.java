package ec.edu.ups.academic_events_api.events.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;
import ec.edu.ups.academic_events_api.categories.repositories.CategoryRepository;
import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.mappers.EventMapper;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.registrations.repositories.RegistrationRepository;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    public EventServiceImpl(
            EventRepository eventRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            RegistrationRepository registrationRepository
    ) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> findAll() {
        return eventRepository.findByDeletedFalse()
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDto findOne(Long id) {
        return EventMapper.toResponseDto(
                findEventById(id)
        );
    }

    @Override
    public EventResponseDto create(CreateEventDto dto) {
        validateDates(
                dto.getStartDate(),
                dto.getEndDate()
        );

        String normalizedTitle =
                dto.getTitle().trim();

        if (eventRepository
                .existsByTitleIgnoreCaseAndDeletedFalse(
                        normalizedTitle
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un evento con ese título"
            );
        }

        CategoryEntity category =
                findCategoryById(dto.getCategoryId());

        UserEntity organizer =
                findOrganizerById(dto.getOrganizerId());

        EventEntity event =
                EventMapper.toEntity(dto);

        event.setTitle(normalizedTitle);
        event.setCategory(category);
        event.setOrganizer(organizer);
        event.setAvailableCapacity(
                dto.getCapacity()
        );

        EventEntity savedEvent =
                eventRepository.save(event);

        return EventMapper.toResponseDto(savedEvent);
    }

    @Override
    public EventResponseDto update(
            Long id,
            UpdateEventDto dto,
            Long authenticatedUserId,
            boolean admin
    ) {
        EventEntity event = findEventById(id);

        validateOwner(
                event,
                authenticatedUserId,
                admin
        );

        validateDates(
                dto.getStartDate(),
                dto.getEndDate()
        );

        String newTitle = dto.getTitle().trim();

        boolean titleChanged =
                !event.getTitle()
                        .equalsIgnoreCase(newTitle);

        if (titleChanged
                && eventRepository
                .existsByTitleIgnoreCaseAndDeletedFalse(
                        newTitle
                )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe otro evento con ese título"
            );
        }

        validateCapacityUpdate(
                event,
                dto.getCapacity()
        );

        CategoryEntity category =
                findCategoryById(dto.getCategoryId());

        EventMapper.updateEntity(event, dto);

        event.setTitle(newTitle);
        event.setCategory(category);

        /*
         * No se cambia el organizador desde el body.
         * Así se evita transferir un evento a otro usuario
         * durante una actualización normal.
         */

        EventEntity updatedEvent =
                eventRepository.save(event);

        return EventMapper.toResponseDto(updatedEvent);
    }

    @Override
    public void delete(
            Long id,
            Long authenticatedUserId,
            boolean admin
    ) {
        EventEntity event = findEventById(id);

        validateOwner(
                event,
                authenticatedUserId,
                admin
        );

        if (registrationRepository.existsByEventId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar un evento que "
                            + "tiene inscripciones"
            );
        }

        event.setDeleted(true);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> findByCategory(
            Long categoryId
    ) {
        findCategoryById(categoryId);

        return eventRepository
                .findByCategoryIdAndDeletedFalse(categoryId)
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> findByOrganizer(
            Long organizerId
    ) {
        findOrganizerById(organizerId);

        return eventRepository
                .findByOrganizerIdAndDeletedFalse(
                        organizerId
                )
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    private void validateOwner(
            EventEntity event,
            Long authenticatedUserId,
            boolean admin
    ) {
        if (admin) {
            return;
        }

        if (authenticatedUserId == null
                || event.getOrganizer() == null
                || event.getOrganizer().getId() == null
                || !event.getOrganizer()
                .getId()
                .equals(authenticatedUserId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Solo el organizador propietario puede "
                            + "modificar este evento"
            );
        }
    }

    private void validateCapacityUpdate(
            EventEntity event,
            Integer newCapacity
    ) {
        int currentCapacity = event.getCapacity();
        int availableCapacity =
                event.getAvailableCapacity();

        int occupiedCapacity =
                currentCapacity - availableCapacity;

        if (newCapacity < occupiedCapacity) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La capacidad no puede ser menor que "
                            + "el número de cupos ocupados"
            );
        }

        event.setCapacity(newCapacity);
        event.setAvailableCapacity(
                newCapacity - occupiedCapacity
        );
    }

    private EventEntity findEventById(Long id) {
        return eventRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Evento no encontrado"
                        )
                );
    }

    private CategoryEntity findCategoryById(Long id) {
        CategoryEntity category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Categoría no encontrada"
                                )
                        );

        if (Boolean.FALSE.equals(category.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría seleccionada está inactiva"
            );
        }

        return category;
    }

    private UserEntity findOrganizerById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Organizador no encontrado"
                        )
                );
    }

    private void validateDates(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y finalización "
                            + "son obligatorias"
            );
        }

        if (!endDate.isAfter(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de finalización debe ser "
                            + "posterior a la fecha de inicio"
            );
        }
    }
}