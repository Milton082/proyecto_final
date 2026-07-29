package ec.edu.ups.academic_events_api.reports.utils;

import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateRegistrationsReport(
            EventEntity event,
            List<RegistrationEntity> registrations
    ) {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet(
                    createSafeSheetName(event.getTitle())
            );

            CellStyle titleStyle =
                    createTitleStyle(workbook);

            CellStyle labelStyle =
                    createLabelStyle(workbook);

            CellStyle headerStyle =
                    createHeaderStyle(workbook);

            CellStyle contentStyle =
                    createContentStyle(workbook);

            CellStyle centeredContentStyle =
                    createCenteredContentStyle(workbook);

            int rowIndex = 0;

            rowIndex = addTitle(
                    sheet,
                    rowIndex,
                    titleStyle
            );

            rowIndex = addEventInformation(
                    sheet,
                    rowIndex,
                    event,
                    labelStyle,
                    contentStyle
            );

            rowIndex++;

            addRegistrationsHeader(
                    sheet,
                    rowIndex,
                    headerStyle
            );

            rowIndex++;

            addRegistrations(
                    sheet,
                    rowIndex,
                    registrations,
                    contentStyle,
                    centeredContentStyle
            );

            configureColumns(sheet);
            sheet.createFreezePane(0, rowIndex);

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el reporte Excel",
                    exception
            );
        }
    }

    private int addTitle(
            Sheet sheet,
            int rowIndex,
            CellStyle titleStyle
    ) {
        Row titleRow = sheet.createRow(rowIndex);

        Cell titleCell = titleRow.createCell(0);

        titleCell.setCellValue(
                "REPORTE DE PARTICIPANTES INSCRITOS"
        );

        titleCell.setCellStyle(titleStyle);

        sheet.addMergedRegion(
                new org.apache.poi.ss.util.CellRangeAddress(
                        rowIndex,
                        rowIndex,
                        0,
                        5
                )
        );

        titleRow.setHeightInPoints(28);

        return rowIndex + 2;
    }

    private int addEventInformation(
            Sheet sheet,
            int rowIndex,
            EventEntity event,
            CellStyle labelStyle,
            CellStyle contentStyle
    ) {
        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Evento",
                event.getTitle(),
                labelStyle,
                contentStyle
        );

        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Ubicación",
                event.getLocation(),
                labelStyle,
                contentStyle
        );

        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Fecha de inicio",
                event.getStartDate() == null
                        ? "No definida"
                        : event.getStartDate().format(DATE_FORMAT),
                labelStyle,
                contentStyle
        );

        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Fecha de finalización",
                event.getEndDate() == null
                        ? "No definida"
                        : event.getEndDate().format(DATE_FORMAT),
                labelStyle,
                contentStyle
        );

        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Organizador",
                getFullName(event.getOrganizer()),
                labelStyle,
                contentStyle
        );

        rowIndex = addInformationRow(
                sheet,
                rowIndex,
                "Estado",
                event.getStatus(),
                labelStyle,
                contentStyle
        );

        return addInformationRow(
                sheet,
                rowIndex,
                "Capacidad",
                String.valueOf(event.getCapacity()),
                labelStyle,
                contentStyle
        );
    }

    private int addInformationRow(
            Sheet sheet,
            int rowIndex,
            String label,
            String value,
            CellStyle labelStyle,
            CellStyle contentStyle
    ) {
        Row row = sheet.createRow(rowIndex);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(
                value == null ? "" : value
        );
        valueCell.setCellStyle(contentStyle);

        sheet.addMergedRegion(
                new org.apache.poi.ss.util.CellRangeAddress(
                        rowIndex,
                        rowIndex,
                        1,
                        5
                )
        );

        return rowIndex + 1;
    }

    private void addRegistrationsHeader(
            Sheet sheet,
            int rowIndex,
            CellStyle headerStyle
    ) {
        Row headerRow = sheet.createRow(rowIndex);

        String[] headers = {
                "#",
                "Participante",
                "Correo",
                "Estado",
                "Fecha de inscripción",
                "Código de registro"
        };

        for (int column = 0;
             column < headers.length;
             column++) {

            Cell cell = headerRow.createCell(column);
            cell.setCellValue(headers[column]);
            cell.setCellStyle(headerStyle);
        }

        headerRow.setHeightInPoints(24);
    }

    private void addRegistrations(
            Sheet sheet,
            int rowIndex,
            List<RegistrationEntity> registrations,
            CellStyle contentStyle,
            CellStyle centeredContentStyle
    ) {
        int position = 1;

        for (RegistrationEntity registration
                : registrations) {

            Row row = sheet.createRow(rowIndex);

            UserEntity participant =
                    registration.getParticipant();

            createCell(
                    row,
                    0,
                    String.valueOf(position),
                    centeredContentStyle
            );

            createCell(
                    row,
                    1,
                    getFullName(participant),
                    contentStyle
            );

            createCell(
                    row,
                    2,
                    participant == null
                            ? ""
                            : participant.getEmail(),
                    contentStyle
            );

            createCell(
                    row,
                    3,
                    registration.getStatus(),
                    centeredContentStyle
            );

            createCell(
                    row,
                    4,
                    registration.getRegisteredAt() == null
                            ? ""
                            : registration
                                    .getRegisteredAt()
                                    .format(DATE_FORMAT),
                    centeredContentStyle
            );

            createCell(
                    row,
                    5,
                    registration.getRegistrationCode() == null
                            ? ""
                            : registration
                                    .getRegistrationCode()
                                    .toString(),
                    contentStyle
            );

            position++;
            rowIndex++;
        }

        if (registrations.isEmpty()) {
            Row row = sheet.createRow(rowIndex);

            Cell cell = row.createCell(0);

            cell.setCellValue(
                    "El evento no tiene participantes inscritos."
            );

            cell.setCellStyle(centeredContentStyle);

            sheet.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            rowIndex,
                            rowIndex,
                            0,
                            5
                    )
            );
        }
    }

    private void createCell(
            Row row,
            int column,
            String value,
            CellStyle style
    ) {
        Cell cell = row.createCell(column);

        cell.setCellValue(
                value == null ? "" : value
        );

        cell.setCellStyle(style);
    }

    private void configureColumns(
            Sheet sheet
    ) {
        sheet.setColumnWidth(0, 8 * 256);
        sheet.setColumnWidth(1, 28 * 256);
        sheet.setColumnWidth(2, 32 * 256);
        sheet.setColumnWidth(3, 16 * 256);
        sheet.setColumnWidth(4, 22 * 256);
        sheet.setColumnWidth(5, 40 * 256);
    }

    private CellStyle createTitleStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        return style;
    }

    private CellStyle createLabelStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);

        style.setFont(font);
        style.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex()
        );
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createHeaderStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();

        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(
                IndexedColors.WHITE.getIndex()
        );

        style.setFont(font);
        style.setFillForegroundColor(
                IndexedColors.DARK_BLUE.getIndex()
        );
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );

        applyBorders(style);

        return style;
    }

    private CellStyle createContentStyle(
            Workbook workbook
    ) {
        CellStyle style = workbook.createCellStyle();

        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );
        style.setWrapText(true);

        applyBorders(style);

        return style;
    }

    private CellStyle createCenteredContentStyle(
            Workbook workbook
    ) {
        CellStyle style = createContentStyle(workbook);

        style.setAlignment(HorizontalAlignment.CENTER);

        return style;
    }

    private void applyBorders(
            CellStyle style
    ) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private String getFullName(
            UserEntity user
    ) {
        if (user == null) {
            return "No definido";
        }

        String firstName =
                user.getFirstName() == null
                        ? ""
                        : user.getFirstName().trim();

        String lastName =
                user.getLastName() == null
                        ? ""
                        : user.getLastName().trim();

        String fullName =
                (firstName + " " + lastName).trim();

        return fullName.isBlank()
                ? "No definido"
                : fullName;
    }

    private String createSafeSheetName(
            String eventTitle
    ) {
        String title = eventTitle == null
                ? "Inscritos"
                : eventTitle;

        title = title.replaceAll(
                "[\\\\/?*\\[\\]:]",
                "-"
        );

        if (title.length() > 31) {
            title = title.substring(0, 31);
        }

        return title.isBlank()
                ? "Inscritos"
                : title;
    }
}