package ec.edu.ups.academic_events_api.events.services;

import ec.edu.ups.academic_events_api.categories.entities.CategoryEntity;
import ec.edu.ups.academic_events_api.categories.repositories.CategoryRepository;
import ec.edu.ups.academic_events_api.events.dtos.CreateEventDto;
import ec.edu.ups.academic_events_api.events.dtos.EventResponseDto;
import ec.edu.ups.academic_events_api.events.dtos.UpdateEventDto;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.events.mappers.EventMapper;
import ec.edu.ups.academic_events_api.events.repositories.EventRepository;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import ec.edu.ups.academic_events_api.users.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(
            EventRepository eventRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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
        EventEntity event = findEventById(id);

        return EventMapper.toResponseDto(event);
    }

    @Override
    public EventResponseDto create(CreateEventDto dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());

        if (eventRepository.existsByTitleIgnoreCaseAndDeletedFalse(
                dto.getTitle().trim()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un evento con ese título"
            );
        }

        CategoryEntity category = findCategoryById(dto.getCategoryId());
        UserEntity organizer = findOrganizerById(dto.getOrganizerId());

        EventEntity event = EventMapper.toEntity(dto);
        event.setCategory(category);
        event.setOrganizer(organizer);

        EventEntity savedEvent = eventRepository.save(event);

        return EventMapper.toResponseDto(savedEvent);
    }

    @Override
    public EventResponseDto update(Long id, UpdateEventDto dto) {
        EventEntity event = findEventById(id);
        validateDates(dto.getStartDate(), dto.getEndDate());
        String newTitle = dto.getTitle().trim();
        boolean titleChanged = !event.getTitle()
                .equalsIgnoreCase(newTitle);

        if (titleChanged
                && eventRepository.existsByTitleIgnoreCaseAndDeletedFalse(newTitle)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe otro evento con ese título"
            );
        }

        CategoryEntity category = findCategoryById(dto.getCategoryId());
        UserEntity organizer = findOrganizerById(dto.getOrganizerId());
        EventMapper.updateEntity(event, dto);
        event.setTitle(newTitle);
        event.setCategory(category);
        event.setOrganizer(organizer);
        EventEntity updatedEvent = eventRepository.save(event);

        return EventMapper.toResponseDto(updatedEvent);
    }

    @Override
    public void delete(Long id) {
        EventEntity event = findEventById(id);
        event.setDeleted(true);
        eventRepository.save(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> findByCategory(Long categoryId) {
        findCategoryById(categoryId);

        return eventRepository.findByCategoryIdAndDeletedFalse(categoryId)
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDto> findByOrganizer(Long organizerId) {
        findOrganizerById(organizerId);

        return eventRepository.findByOrganizerIdAndDeletedFalse(organizerId)
                .stream()
                .map(EventMapper::toResponseDto)
                .toList();
    }

    private EventEntity findEventById(Long id) {
        return eventRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Evento no encontrado"
                ));
    }

    private CategoryEntity findCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                ));

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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Organizador no encontrado"
                ));
    }

    private void validateDates(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if (startDate == null || endDate == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las fechas de inicio y finalización son obligatorias"
            );
        }

        if (!endDate.isAfter(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de finalización debe ser posterior a la fecha de inicio"
            );
        }
    }
}