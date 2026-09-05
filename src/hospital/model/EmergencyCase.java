package hospital.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A patient who has arrived at the emergency unit and is waiting for treatment.
 *
 * <p>The case keeps the arrival time only for reporting. The waiting order is decided
 * purely by the {@code EmergencyQueue} (FIFO), never by this field.</p>
 */
public class EmergencyCase {

    public static final String TABLE_LINE =
            "+-----+---------+--------+----------------------+------------------------+---------------------+";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String caseId;
    private final Patient patient;
    private final String reason;
    private final LocalDateTime arrivalTime;

    public EmergencyCase(String caseId, Patient patient, String reason) {
        this(caseId, patient, reason, LocalDateTime.now());
    }

    public EmergencyCase(String caseId, Patient patient, String reason, LocalDateTime arrivalTime) {
        this.caseId = caseId;
        this.patient = patient;
        this.reason = reason;
        this.arrivalTime = arrivalTime;
    }

    public String getCaseId() {
        return caseId;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getFormattedArrivalTime() {
        return arrivalTime.format(TIME_FORMAT);
    }

    public static String tableHeader() {
        return TABLE_LINE + System.lineSeparator()
                + String.format("| %-3s | %-7s | %-6s | %-20s | %-22s | %-19s |",
                        "POS", "CASE ID", "PT ID", "PATIENT NAME", "REASON", "ARRIVAL TIME")
                + System.lineSeparator() + TABLE_LINE;
    }

    /**
     * @param position 1 based place in the waiting line, 1 means "treated next"
     */
    public String toTableRow(int position) {
        return String.format("| %-3d | %-7s | %-6d | %-20s | %-22s | %-19s |",
                position, caseId, patient.getPatientId(), trim(patient.getName(), 20),
                trim(reason, 22), getFormattedArrivalTime());
    }

    private static String trim(String value, int width) {
        if (value == null) {
            return "-";
        }
        return value.length() <= width ? value : value.substring(0, width - 3) + "...";
    }

    @Override
    public String toString() {
        return caseId + " - " + patient.getName() + " (" + reason + ")";
    }
}
