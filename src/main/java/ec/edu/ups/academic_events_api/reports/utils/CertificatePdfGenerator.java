package ec.edu.ups.academic_events_api.reports.utils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import ec.edu.ups.academic_events_api.events.entities.EventEntity;
import ec.edu.ups.academic_events_api.registrations.entities.RegistrationEntity;
import ec.edu.ups.academic_events_api.users.entities.UserEntity;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CertificatePdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateCertificate(
            RegistrationEntity registration
    ) {
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document = new Document(
                PageSize.A4.rotate(),
                70,
                70,
                60,
                60
        );

        try {
            PdfWriter.getInstance(document, outputStream);

            document.addTitle("Certificado de participación");
            document.addSubject(
                    "Certificado generado para la inscripción "
                            + registration.getRegistrationCode()
            );

            document.open();

            addCertificateContent(document, registration);

        } catch (DocumentException exception) {
            throw new IllegalStateException(
                    "No se pudo generar el certificado PDF",
                    exception
            );
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return outputStream.toByteArray();
    }

    private void addCertificateContent(
            Document document,
            RegistrationEntity registration
    ) throws DocumentException {

        EventEntity event = registration.getEvent();
        UserEntity participant = registration.getParticipant();

        Font institutionFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                15
        );

        Font certificateFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                28
        );

        Font normalFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                15
        );

        Font participantFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                23
        );

        Font eventFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18
        );

        Font footerFont = FontFactory.getFont(
                FontFactory.HELVETICA,
                10
        );

        Paragraph institution = new Paragraph(
                "UNIVERSIDAD POLITÉCNICA SALESIANA",
                institutionFont
        );

        institution.setAlignment(Element.ALIGN_CENTER);
        institution.setSpacingAfter(25);
        document.add(institution);

        Paragraph certificateTitle = new Paragraph(
                "CERTIFICADO DE PARTICIPACIÓN",
                certificateFont
        );

        certificateTitle.setAlignment(Element.ALIGN_CENTER);
        certificateTitle.setSpacingAfter(35);
        document.add(certificateTitle);

        Paragraph certificationText = new Paragraph(
                "Se certifica que:",
                normalFont
        );

        certificationText.setAlignment(Element.ALIGN_CENTER);
        certificationText.setSpacingAfter(15);
        document.add(certificationText);

        Paragraph participantName = new Paragraph(
                getFullName(participant),
                participantFont
        );

        participantName.setAlignment(Element.ALIGN_CENTER);
        participantName.setSpacingAfter(25);
        document.add(participantName);

        Paragraph participationText = new Paragraph(
                "participó satisfactoriamente en el evento académico:",
                normalFont
        );

        participationText.setAlignment(Element.ALIGN_CENTER);
        participationText.setSpacingAfter(15);
        document.add(participationText);

        Paragraph eventTitle = new Paragraph(
                event.getTitle(),
                eventFont
        );

        eventTitle.setAlignment(Element.ALIGN_CENTER);
        eventTitle.setSpacingAfter(25);
        document.add(eventTitle);

        String eventDetails =
                "Realizado el "
                        + formatEventDate(event.getStartDate())
                        + " en "
                        + getLocation(event);

        Paragraph details = new Paragraph(
                eventDetails,
                normalFont
        );

        details.setAlignment(Element.ALIGN_CENTER);
        details.setSpacingAfter(35);
        document.add(details);

        Paragraph issueDate = new Paragraph(
                "Certificado emitido el "
                        + OffsetDateTime.now().format(DATE_FORMAT),
                normalFont
        );

        issueDate.setAlignment(Element.ALIGN_CENTER);
        issueDate.setSpacingAfter(40);
        document.add(issueDate);

        Paragraph registrationCode = new Paragraph(
                "Código de inscripción: "
                        + registration.getRegistrationCode(),
                footerFont
        );

        registrationCode.setAlignment(Element.ALIGN_CENTER);
        registrationCode.setSpacingAfter(5);
        document.add(registrationCode);

        Paragraph verificationText = new Paragraph(
                "Estado de la inscripción: "
                        + registration.getStatus(),
                footerFont
        );

        verificationText.setAlignment(Element.ALIGN_CENTER);
        document.add(verificationText);
    }

    private String getFullName(
            UserEntity user
    ) {
        if (user == null) {
            return "Participante no definido";
        }

        String firstName = user.getFirstName() == null
                ? ""
                : user.getFirstName().trim();

        String lastName = user.getLastName() == null
                ? ""
                : user.getLastName().trim();

        String fullName =
                (firstName + " " + lastName).trim();

        return fullName.isBlank()
                ? "Participante no definido"
                : fullName;
    }

    private String formatEventDate(
            LocalDateTime eventDate
    ) {
        if (eventDate == null) {
            return "una fecha no definida";
        }

        return eventDate.format(DATE_FORMAT);
    }

    private String getLocation(
            EventEntity event
    ) {
        if (event.getLocation() == null
                || event.getLocation().isBlank()) {
            return "una ubicación no definida";
        }

        return event.getLocation();
    }
}