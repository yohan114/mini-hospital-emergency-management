package hospital.model;

/**
 * A single patient record held by the hospital system.
 *
 * <p>The {@code patientId} is the key used by the Binary Search Tree, therefore it is
 * declared {@code final}: changing the key of a node after insertion would break the
 * ordering property of the tree.</p>
 */
public class Patient {

    /** Width of every column used by the listing table, kept in one place. */
    public static final String TABLE_LINE =
            "+--------+----------------------+------+----------------+------------------------+";

    private final int patientId;
    private String name;
    private int age;
    private String contactNumber;
    private String medicalCondition;

    public Patient(int patientId, String name, int age, String contactNumber, String medicalCondition) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
        this.medicalCondition = medicalCondition;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMedicalCondition() {
        return medicalCondition;
    }

    public void setMedicalCondition(String medicalCondition) {
        this.medicalCondition = medicalCondition;
    }

    /** Header row of the patient listing table. */
    public static String tableHeader() {
        return TABLE_LINE + System.lineSeparator()
                + String.format("| %-6s | %-20s | %-4s | %-14s | %-22s |",
                        "ID", "NAME", "AGE", "CONTACT", "MEDICAL CONDITION")
                + System.lineSeparator() + TABLE_LINE;
    }

    /** One formatted row describing this patient. */
    public String toTableRow() {
        return String.format("| %-6d | %-20s | %-4d | %-14s | %-22s |",
                patientId, trim(name, 20), age, trim(contactNumber, 14), trim(medicalCondition, 22));
    }

    /** Multi line view used when a single patient is displayed on its own. */
    public String toDetailedString() {
        return "  Patient ID        : " + patientId + System.lineSeparator()
                + "  Name              : " + name + System.lineSeparator()
                + "  Age               : " + age + System.lineSeparator()
                + "  Contact Number    : " + contactNumber + System.lineSeparator()
                + "  Medical Condition : " + medicalCondition;
    }

    /** Keeps table columns aligned when a value is longer than its column. */
    private static String trim(String value, int width) {
        if (value == null) {
            return "-";
        }
        return value.length() <= width ? value : value.substring(0, width - 3) + "...";
    }

    @Override
    public String toString() {
        return "[" + patientId + "] " + name + " (" + age + ") - " + medicalCondition;
    }
}
