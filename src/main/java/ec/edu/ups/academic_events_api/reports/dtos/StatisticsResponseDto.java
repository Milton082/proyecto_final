package ec.edu.ups.academic_events_api.reports.dtos;

public class StatisticsResponseDto {

    private Long totalEvents;
    private Long totalRegistrations;
    private Long confirmedRegistrations;
    private Long pendingRegistrations;
    private Long cancelledRegistrations;
    private Long rejectedRegistrations;

    public StatisticsResponseDto() {
    }

    public Long getTotalEvents() {
        return totalEvents;
    }

    public void setTotalEvents(Long totalEvents) {
        this.totalEvents = totalEvents;
    }

    public Long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(
            Long totalRegistrations
    ) {
        this.totalRegistrations = totalRegistrations;
    }

    public Long getConfirmedRegistrations() {
        return confirmedRegistrations;
    }

    public void setConfirmedRegistrations(
            Long confirmedRegistrations
    ) {
        this.confirmedRegistrations =
                confirmedRegistrations;
    }

    public Long getPendingRegistrations() {
        return pendingRegistrations;
    }

    public void setPendingRegistrations(
            Long pendingRegistrations
    ) {
        this.pendingRegistrations =
                pendingRegistrations;
    }

    public Long getCancelledRegistrations() {
        return cancelledRegistrations;
    }

    public void setCancelledRegistrations(
            Long cancelledRegistrations
    ) {
        this.cancelledRegistrations =
                cancelledRegistrations;
    }

    public Long getRejectedRegistrations() {
        return rejectedRegistrations;
    }

    public void setRejectedRegistrations(Long rejectedRegistrations) {
        this.rejectedRegistrations = rejectedRegistrations;
    }
    
}