package ec.edu.ups.academic_events_api.sessions.mappers;

import ec.edu.ups.academic_events_api.sessions.dtos.*;
import ec.edu.ups.academic_events_api.sessions.entities.SessionEntity;

public class SessionMapper {

    public static SessionEntity toEntity(CreateSessionDto dto){

        SessionEntity entity=new SessionEntity();

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());
        entity.setLocation(dto.getLocation());
        entity.setVirtualUrl(dto.getVirtualUrl());

        return entity;

    }

    public static void update(SessionEntity entity,UpdateSessionDto dto){

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStartAt(dto.getStartAt());
        entity.setEndAt(dto.getEndAt());
        entity.setLocation(dto.getLocation());
        entity.setVirtualUrl(dto.getVirtualUrl());

    }

    public static SessionResponseDto toDto(SessionEntity entity){

        SessionResponseDto dto=new SessionResponseDto();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartAt(entity.getStartAt());
        dto.setEndAt(entity.getEndAt());
        dto.setLocation(entity.getLocation());
        dto.setVirtualUrl(entity.getVirtualUrl());

        dto.setEventId(entity.getEvent().getId());
        dto.setEventTitle(entity.getEvent().getTitle());

        return dto;

    }

}