package ec.edu.ups.academic_events_api.reports.controllers;

import ec.edu.ups.academic_events_api.reports.dtos.StatisticsResponseDto;
import ec.edu.ups.academic_events_api.reports.services.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponseDto>
    statistics(
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            OffsetDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            OffsetDateTime endDate
    ) {
        StatisticsResponseDto response =
                reportService.getStatistics(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(response);
    }
}