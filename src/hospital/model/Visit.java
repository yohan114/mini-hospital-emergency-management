package hospital.model;

/**
 * One past hospital visit of a patient.
 *
 * <p>Visits are stored inside the singly linked list that every {@link Patient} owns,
 * so a visit only has to describe itself - the link to the next visit is kept by the
 * list node, not by this class.</p>
 */
public class Visit {

    public static final String TABLE_LINE =
            "+----------+------------+--------------------+------------------------+------------------------+";

    private final String visitId;
    private String visitDate;
    private String doctorName;
    private String diagnosis;
    private String treatment;

    public Visit(String visitId, String visitDate, String doctorName, String diagnosis, String treatment) {
        this.visitId = visitId;
        this.visitDate = visitDate;
        this.doctorName = doctorName;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public String getVisitId() {
        return visitId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public static String tableHeader() {
        return TABLE_LINE + System.lineSeparator()
                + String.format("| %-8s | %-10s | %-18s | %-22s | %-22s |",
                        "VISIT ID", "DATE", "DOCTOR", "DIAGNOSIS", "TREATMENT")
                + System.lineSeparator() + TABLE_LINE;
    }

    public String toTableRow() {
        return String.format("| %-8s | %-10s | %-18s | %-22s | %-22s |",
                trim(visitId, 8), trim(visitDate, 10), trim(doctorName, 18),
                trim(diagnosis, 22), trim(treatment, 22));
    }

    private static String trim(String value, int width) {
        if (value == null) {
            return "-";
        }
        return value.length() <= width ? value : value.substring(0, width - 3) + "...";
    }

    @Override
    public String toString() {
        return visitId + " | " + visitDate + " | " + doctorName + " | " + diagnosis;
    }
}
