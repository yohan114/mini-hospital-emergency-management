package hospital.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A completed treatment. Records are pushed onto the treatment history stack the moment
 * a patient leaves the emergency queue and the treatment is finished.
 *
 * <p>{@code visitId} links the record back to the {@link Visit} that was appended to the
 * patient's visit history, so that popping a record can also undo that visit.</p>
 */
public class TreatmentRecord {

    public static final String TABLE_LINE =
            "+-----+---------+--------+----------------------+--------------------+------------------------+---------------------+";

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String recordId;
    private final int patientId;
    private final String patientName;
    private final String doctorName;
    private final String treatmentGiven;
    private final String visitId;
    private final LocalDateTime completedAt;

    public TreatmentRecord(String recordId, int patientId, String patientName,
                           String doctorName, String treatmentGiven, String visitId) {
        this(recordId, patientId, patientName, doctorName, treatmentGiven, visitId, LocalDateTime.now());
    }

    public TreatmentRecord(String recordId, int patientId, String patientName, String doctorName,
                           String treatmentGiven, String visitId, LocalDateTime completedAt) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.treatmentGiven = treatmentGiven;
        this.visitId = visitId;
        this.completedAt = completedAt;
    }

    public String getRecordId() {
        return recordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getTreatmentGiven() {
        return treatmentGiven;
    }

    /** May be {@code null} when the record was pushed manually without a linked visit. */
    public String getVisitId() {
        return visitId;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getFormattedCompletedAt() {
        return completedAt.format(TIME_FORMAT);
    }

    public static String tableHeader() {
        return TABLE_LINE + System.lineSeparator()
                + String.format("| %-3s | %-7s | %-6s | %-20s | %-18s | %-22s | %-19s |",
                        "LVL", "REC ID", "PT ID", "PATIENT NAME", "DOCTOR", "TREATMENT GIVEN", "COMPLETED AT")
                + System.lineSeparator() + TABLE_LINE;
    }

    /**
     * @param level 1 based distance from the top of the stack, 1 means "most recent"
     */
    public String toTableRow(int level) {
        return String.format("| %-3d | %-7s | %-6d | %-20s | %-18s | %-22s | %-19s |",
                level, recordId, patientId, trim(patientName, 20), trim(doctorName, 18),
                trim(treatmentGiven, 22), getFormattedCompletedAt());
    }

    private static String trim(String value, int width) {
        if (value == null) {
            return "-";
        }
        return value.length() <= width ? value : value.substring(0, width - 3) + "...";
    }

    @Override
    public String toString() {
        return recordId + " - " + patientName + " treated by " + doctorName;
    }
}
