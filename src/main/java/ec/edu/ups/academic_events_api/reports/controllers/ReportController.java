package ec.edu.ups.academic_events_api.reports.controllers;

import ec.edu.ups.academic_events_api.reports.dtos.StatisticsResponseDto;
import ec.edu.ups.academic_events_api.reports.services.ReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.nio.charset.StandardCharsets;

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
    public ResponseEntity<StatisticsResponseDto> statistics(
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
        return ResponseEntity.ok(
                reportService.getStatistics(
                        startDate,
                        endDate
                )
        );
    }

    @GetMapping(
            value = "/events/{eventId}/registrations/pdf",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> downloadRegistrationsPdf(
            @PathVariable Long eventId
    ) {
        byte[] pdf =
                reportService.generateRegistrationsPdf(
                        eventId
                );

        String fileName =
                "inscritos-evento-" + eventId + ".pdf";

        ContentDisposition contentDisposition =
                ContentDisposition
                        .attachment()
                        .filename(
                                fileName,
                                StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        contentDisposition.toString()
                )
                .contentLength(pdf.length)
                .body(pdf);
    }

    private static final MediaType EXCEL_MEDIA_TYPE =
        MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument."
                        + "spreadsheetml.sheet"
        );

    @GetMapping(
        value = "/events/{eventId}/registrations/excel",
        produces = "application/vnd.openxmlformats-officedocument."
                + "spreadsheetml.sheet"
)
public ResponseEntity<byte[]> downloadRegistrationsExcel(
        @PathVariable Long eventId
) {
    byte[] excel =
            reportService.generateRegistrationsExcel(
                    eventId
            );

    String fileName =
            "inscritos-evento-" + eventId + ".xlsx";

    ContentDisposition contentDisposition =
            ContentDisposition
                    .attachment()
                    .filename(
                            fileName,
                            StandardCharsets.UTF_8
                    )
                    .build();

    return ResponseEntity.ok()
            .contentType(EXCEL_MEDIA_TYPE)
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    contentDisposition.toString()
            )
            .contentLength(excel.length)
            .body(excel);
    }
}