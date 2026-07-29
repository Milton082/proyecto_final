package ec.edu.ups.academic_events_api.reports.utils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import org.springframework.stereotype.Component;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PdfGenerator {

    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final DateTimeFormatter REGISTRATION_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateRegistrationsReport(
            EventEntity event,
            List<RegistrationEntity> registrations
    ) {
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4.rotate(),
                36,
                36,
                36,
                36
        );

        try {
            PdfWriter.getInstance(document, outputStream);

            document.addTitle(
                    "Reporte de inscritos - " + event.getTitle()
            );

            document.addSubject(
                    "Listado de participantes inscritos"
            );

            document.open();

            addReportTitle(document);
            addEventInformation(document, event);
            addRegistrationsTable(document, registrations);
            addSummary(document, registrations);

        } catch (DocumentException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el reporte PDF",
                    exception
            );
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return outputStream.toByteArray();
    }

    private void addReportTitle(
            Document document
    ) throws DocumentException {
        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18
        );

        Paragraph title = new Paragraph(
                "REPORTE DE PARTICIPANTES INSCRITOS",
                titleFont
        );

        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(18);

        document.add(title);
    }

    private void addEventInformation(
            Document document,
            EventEntity event
    ) throws DocumentException {
        Font labelFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                11
        );

        Font valueFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                11
        );

        PdfPTable eventTable = new PdfPTable(2);

        eventTable.setWidthPercentage(100);
        eventTable.setWidths(new float[]{1.5f, 4.5f});
        eventTable.setSpacingAfter(18);

        addInformationRow(
                eventTable,
                "Evento:",
                event.getTitle(),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Ubicación:",
                event.getLocation(),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Fecha de inicio:",
                event.getStartDate() == null
                        ? "No definida"
                        : event.getStartDate()
                                .format(EVENT_DATE_FORMAT),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Fecha de finalización:",
                event.getEndDate() == null
                        ? "No definida"
                        : event.getEndDate()
                                .format(EVENT_DATE_FORMAT),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Organizador:",
                getFullName(event.getOrganizer()),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Estado:",
                event.getStatus(),
                labelFont,
                valueFont
        );

        addInformationRow(
                eventTable,
                "Capacidad:",
                String.valueOf(event.getCapacity()),
                labelFont,
                valueFont
        );

        document.add(eventTable);
    }

    private void addInformationRow(
            PdfPTable table,
            String label,
            String value,
            Font labelFont,
            Font valueFont
    ) {
        PdfPCell labelCell = new PdfPCell(
                new Phrase(label, labelFont)
        );

        labelCell.setPadding(6);
        labelCell.setBorderWidth(0);

        PdfPCell valueCell = new PdfPCell(
                new Phrase(
                        value == null ? "" : value,
                        valueFont
                )
        );

        valueCell.setPadding(6);
        valueCell.setBorderWidth(0);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addRegistrationsTable(
            Document document,
            List<RegistrationEntity> registrations
    ) throws DocumentException {
        Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                10,
                Color.WHITE
        );

        Font contentFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                9
        );

        PdfPTable table = new PdfPTable(6);

        table.setWidthPercentage(100);
        table.setWidths(
                new float[]{
                        0.6f,
                        2.2f,
                        2.4f,
                        1.2f,
                        1.7f,
                        2.2f
                }
        );

        table.setHeaderRows(1);

        addHeaderCell(table, "#", headerFont);
        addHeaderCell(table, "Participante", headerFont);
        addHeaderCell(table, "Correo", headerFont);
        addHeaderCell(table, "Estado", headerFont);
        addHeaderCell(table, "Fecha", headerFont);
        addHeaderCell(table, "Código", headerFont);

        int position = 1;

        for (RegistrationEntity registration : registrations) {
            UserEntity participant =
                    registration.getParticipant();

            addContentCell(
                    table,
                    String.valueOf(position),
                    contentFont
            );

            addContentCell(
                    table,
                    getFullName(participant),
                    contentFont
            );

            addContentCell(
                    table,
                    participant.getEmail(),
                    contentFont
            );

            addContentCell(
                    table,
                    registration.getStatus(),
                    contentFont
            );

            addContentCell(
                    table,
                    registration.getRegisteredAt() == null
                            ? ""
                            : registration.getRegisteredAt()
                                    .format(
                                            REGISTRATION_DATE_FORMAT
                                    ),
                    contentFont
            );

            addContentCell(
                    table,
                    registration.getRegistrationCode() == null
                            ? ""
                            : registration
                                    .getRegistrationCode()
                                    .toString(),
                    contentFont
            );

            position++;
        }

        if (registrations.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(
                    new Phrase(
                            "El evento no tiene participantes inscritos.",
                            contentFont
                    )
            );

            emptyCell.setColspan(6);
            emptyCell.setHorizontalAlignment(
                    Element.ALIGN_CENTER
            );
            emptyCell.setPadding(12);

            table.addCell(emptyCell);
        }

        document.add(table);
    }

    private void addHeaderCell(
            PdfPTable table,
            String value,
            Font font
    ) {
        PdfPCell cell = new PdfPCell(
                new Phrase(value, font)
        );

        cell.setBackgroundColor(new Color(60, 60, 60));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7);

        table.addCell(cell);
    }

    private void addContentCell(
            PdfPTable table,
            String value,
            Font font
    ) {
        PdfPCell cell = new PdfPCell(
                new Phrase(
                        value == null ? "" : value,
                        font
                )
        );

        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);

        table.addCell(cell);
    }

    private void addSummary(
            Document document,
            List<RegistrationEntity> registrations
    ) throws DocumentException {
        Font summaryFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                11
        );

        Paragraph summary = new Paragraph(
                "Total de inscripciones: "
                        + registrations.size(),
                summaryFont
        );

        summary.setSpacingBefore(14);
        summary.setAlignment(Element.ALIGN_RIGHT);

        document.add(summary);
    }

    private String getFullName(
            UserEntity user
    ) {
        if (user == null) {
            return "No definido";
        }

        String firstName = user.getFirstName() == null
                ? ""
                : user.getFirstName().trim();

        String lastName = user.getLastName() == null
                ? ""
                : user.getLastName().trim();

        String fullName = (
                firstName + " " + lastName
        ).trim();

        return fullName.isBlank()
                ? "No definido"
                : fullName;
    }
}