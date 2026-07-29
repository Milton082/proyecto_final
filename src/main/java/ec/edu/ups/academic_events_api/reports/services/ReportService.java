package ec.edu.ups.academic_events_api.reports.services;

import ec.edu.ups.academic_events_api.reports.dtos.StatisticsResponseDto;
import java.time.OffsetDateTime;

public interface ReportService {

    StatisticsResponseDto getStatistics(
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );

    byte[] generateRegistrationsPdf(Long eventId);
    byte[] generateRegistrationsExcel(Long eventId);
}