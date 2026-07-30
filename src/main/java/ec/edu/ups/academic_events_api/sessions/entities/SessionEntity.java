package ec.edu.ups.academic_events_api.sessions.entities;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(nullable = false,length = 160)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name="start_at",nullable = false)
    private LocalDateTime startAt;

    @Column(name="end_at",nullable = false)
    private LocalDateTime endAt;

    @Column(length = 200)
    private String location;

    @Column(name="virtual_url",length = 500)
    private String virtualUrl;

    public SessionEntity(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public EventEntity getEvent() {
        return event;
    }

    public void setEvent(EventEntity event) {
        this.event=event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt){
        this.startAt=startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt){
        this.endAt=endAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location){
        this.location=location;
    }

    public String getVirtualUrl() {
        return virtualUrl;
    }

    public void setVirtualUrl(String virtualUrl){
        this.virtualUrl=virtualUrl;
    }
}