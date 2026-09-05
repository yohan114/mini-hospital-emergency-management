package hospital.service;

import hospital.datastructures.EmergencyQueue;
import hospital.datastructures.PatientBST;
import hospital.datastructures.TreatmentStack;
import hospital.datastructures.VisitLinkedList;
import hospital.model.EmergencyCase;
import hospital.model.Patient;
import hospital.model.TreatmentRecord;
import hospital.model.Visit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service layer of the hospital system. It owns the four data structures and contains the
 * hospital workflow, so that the menu class only has to deal with input and output.
 *
 * <p>How the structures work together in the main flow:</p>
 * <pre>
 *   register patient      -&gt; BST insert
 *   arrive at emergency   -&gt; Queue enqueue
 *   treat next patient    -&gt; Queue dequeue  +  Linked list add visit  +  Stack push record
 *   undo last treatment   -&gt; Stack pop      +  Linked list remove that visit
 * </pre>
 */
public class HospitalSystem {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final PatientBST patientRecords = new PatientBST();
    private final EmergencyQueue emergencyQueue = new EmergencyQueue();
    private final TreatmentStack treatmentHistory = new TreatmentStack();

    // Counters behind the generated IDs. Sequential IDs keep the demo output readable.
    private int nextCaseNumber = 1;
    private int nextRecordNumber = 1;
    private int nextVisitNumber = 1;

    private boolean sampleDataLoaded;

    // ------------------------------------------------------------------
    // 1. Patient records - Binary Search Tree
    // ------------------------------------------------------------------

    /**
     * Registers a new patient.
     *
     * @return {@code false} when the Patient ID is already used
     */
    public boolean registerPatient(int patientId, String name, int age,
                                   String contactNumber, String medicalCondition) {
        Patient patient = new Patient(patientId, name, age, contactNumber, medicalCondition);
        return patientRecords.insert(patient);
    }

    /** @return the patient with this ID, or {@code null} when there is no such record */
    public Patient findPatient(int patientId) {
        return patientRecords.search(patientId);
    }

    /** The chain of IDs a BST search compares, shown in the menu to explain the lookup. */
    public String describeSearchPath(int patientId) {
        return patientRecords.searchPath(patientId);
    }

    public boolean isWaitingInEmergencyQueue(int patientId) {
        return emergencyQueue.containsPatient(patientId);
    }

    /**
     * Deletes a patient record. The patient is also taken out of the emergency queue if
     * they were still waiting, otherwise the queue would hold a case whose record no
     * longer exists.
     *
     * <p>Treatment records already on the stack are deliberately kept: the stack is the
     * hospital's treatment log and must stay complete even after a record is removed.</p>
     *
     * @return {@code false} when the Patient ID was not found
     */
    public boolean deletePatient(int patientId) {
        if (!patientRecords.contains(patientId)) {
            return false;
        }
        emergencyQueue.removeByPatientId(patientId);
        return patientRecords.delete(patientId);
    }

    public void displayAllPatients() {
        patientRecords.displayInOrder();
    }

    public void displayTreeStructure() {
        patientRecords.displayTreeStructure();
    }

    // ------------------------------------------------------------------
    // 2. Emergency unit - Queue
    // ------------------------------------------------------------------

    /** Puts an already registered patient at the end of the emergency waiting line. */
    public EmergencyCase admitToEmergency(Patient patient, String reason) {
        EmergencyCase emergencyCase = new EmergencyCase(nextCaseId(), patient, reason);
        emergencyQueue.enqueue(emergencyCase);
        return emergencyCase;
    }

    /** @throws hospital.datastructures.EmptyStructureException when nobody is waiting */
    public EmergencyCase viewNextPatient() {
        return emergencyQueue.peek();
    }

    /**
     * Completes the treatment of the patient at the front of the queue. This is the step
     * where all four structures meet: the case leaves the queue, a visit is appended to
     * the patient's linked list and a record is pushed onto the treatment stack.
     *
     * @throws hospital.datastructures.EmptyStructureException when nobody is waiting
     */
    public TreatmentRecord treatNextPatient(String doctorName, String diagnosis, String treatmentGiven) {
        EmergencyCase treatedCase = emergencyQueue.dequeue();
        Patient patient = treatedCase.getPatient();

        Visit visit = new Visit(nextVisitId(), LocalDateTime.now().format(DATE_FORMAT),
                doctorName, diagnosis, treatmentGiven);
        patient.getVisitHistory().addVisit(visit);

        TreatmentRecord record = new TreatmentRecord(nextRecordId(), patient.getPatientId(),
                patient.getName(), doctorName, treatmentGiven, visit.getVisitId());
        treatmentHistory.push(record);
        return record;
    }

    /**
     * Removes a waiting patient without treating them, for example when they leave the
     * hospital before being seen.
     *
     * @return the cancelled case, or {@code null} when the patient was not in the queue
     */
    public EmergencyCase cancelEmergencyCase(int patientId) {
        return emergencyQueue.removeByPatientId(patientId);
    }

    public void displayEmergencyQueue() {
        emergencyQueue.display();
    }

    public boolean isEmergencyQueueEmpty() {
        return emergencyQueue.isEmpty();
    }

    // ------------------------------------------------------------------
    // 3. Treatment history - Stack
    // ------------------------------------------------------------------

    /** Pushes a record for a treatment that did not come through the emergency queue. */
    public TreatmentRecord recordTreatmentDirectly(Patient patient, String doctorName, String treatmentGiven) {
        TreatmentRecord record = new TreatmentRecord(nextRecordId(), patient.getPatientId(),
                patient.getName(), doctorName, treatmentGiven, null);
        treatmentHistory.push(record);
        return record;
    }

    /** @throws hospital.datastructures.EmptyStructureException when the history is empty */
    public TreatmentRecord viewLastTreatment() {
        return treatmentHistory.peek();
    }

    /**
     * Pops the most recent treatment record. When that record was created through the
     * emergency flow, the visit it produced is also removed from the patient's linked
     * list, so an entry made by mistake can be undone completely.
     *
     * @throws hospital.datastructures.EmptyStructureException when the history is empty
     */
    public TreatmentRecord undoLastTreatment() {
        TreatmentRecord record = treatmentHistory.pop();
        String visitId = record.getVisitId();
        if (visitId != null) {
            Patient patient = patientRecords.search(record.getPatientId());
            if (patient != null) {
                patient.getVisitHistory().removeVisit(visitId);
            }
        }
        return record;
    }

    public void displayTreatmentHistory() {
        treatmentHistory.display();
    }

    public boolean isTreatmentHistoryEmpty() {
        return treatmentHistory.isEmpty();
    }

    // ------------------------------------------------------------------
    // 4. Patient visit history - Singly Linked List
    // ------------------------------------------------------------------

    /** Appends a visit to the history of one patient. The Visit ID is generated here. */
    public Visit addVisit(Patient patient, String visitDate, String doctorName,
                          String diagnosis, String treatment) {
        Visit visit = new Visit(nextVisitId(), visitDate, doctorName, diagnosis, treatment);
        patient.getVisitHistory().addVisit(visit);
        return visit;
    }

    /** @return the removed visit, or {@code null} when the Visit ID was not found */
    public Visit removeVisit(Patient patient, String visitId) {
        return patient.getVisitHistory().removeVisit(visitId);
    }

    /** @return the matching visit, or {@code null} when the Visit ID was not found */
    public Visit searchVisit(Patient patient, String visitId) {
        return patient.getVisitHistory().searchVisit(visitId);
    }

    public void displayVisitHistory(Patient patient) {
        patient.getVisitHistory().display();
    }

    // ------------------------------------------------------------------
    // Reporting and sample data
    // ------------------------------------------------------------------

    /** Total number of visits recorded across every patient in the tree. */
    public int countAllVisits() {
        int total = 0;
        for (Patient patient : patientRecords.toSortedArray()) {
            total += patient.getVisitHistory().size();
        }
        return total;
    }

    public void displaySystemSummary() {
        System.out.println("  Structure            Module                  Size");
        System.out.println("  -------------------- ----------------------- --------------------");
        System.out.printf("  Binary Search Tree   Patient records         %d patient(s)%n", patientRecords.size());
        System.out.printf("  Queue (FIFO)         Emergency unit          %d waiting%n", emergencyQueue.size());
        System.out.printf("  Stack (LIFO)         Treatment history       %d record(s)%n", treatmentHistory.size());
        System.out.printf("  Singly Linked List   Patient visit history   %d visit(s) in total%n", countAllVisits());
        System.out.println();

        if (patientRecords.isEmpty()) {
            System.out.println("  No patient records yet - register a patient or load the sample data.");
            return;
        }

        Patient lowest = patientRecords.findMin();
        Patient highest = patientRecords.findMax();
        System.out.println("  Lowest  Patient ID : " + lowest.getPatientId() + " (" + lowest.getName() + ")");
        System.out.println("  Highest Patient ID : " + highest.getPatientId() + " (" + highest.getName() + ")");
        System.out.println("  BST height         : " + patientRecords.height()
                + "  (a search compares at most " + (patientRecords.height() + 1) + " record(s))");

        if (!emergencyQueue.isEmpty()) {
            System.out.println("  Next to be treated : " + emergencyQueue.peek().getPatient().getName()
                    + " (" + emergencyQueue.peek().getCaseId() + ")");
        }
        if (!treatmentHistory.isEmpty()) {
            System.out.println("  Last treatment     : " + treatmentHistory.peek().getRecordId()
                    + " - " + treatmentHistory.peek().getPatientName());
        }
    }

    public boolean isSampleDataLoaded() {
        return sampleDataLoaded;
    }

    /**
     * Fills the system with a small, realistic data set so that every structure can be
     * demonstrated without typing records by hand.
     *
     * <p>The IDs are inserted in the order 105, 102, 108, 101, 104, 107, 110, 103 on
     * purpose: that produces a properly branched tree rather than one long chain, which
     * makes the in-order traversal and the tree drawing meaningful.</p>
     */
    public void loadSampleData() {
        if (sampleDataLoaded) {
            return;
        }
        sampleDataLoaded = true;

        registerPatient(105, "Nimal Perera", 45, "0771234567", "Chest pain");
        registerPatient(102, "Kamala Silva", 32, "0712345678", "Fractured arm");
        registerPatient(108, "Sunil Fernando", 67, "0763456789", "High blood pressure");
        registerPatient(101, "Anusha Jayasinghe", 28, "0754567890", "Severe migraine");
        registerPatient(104, "Ravi Bandara", 51, "0785678901", "Diabetes");
        registerPatient(107, "Malini Gunawardena", 39, "0776789012", "Asthma attack");
        registerPatient(110, "Dinesh Rathnayake", 23, "0727890123", "Road accident injury");
        registerPatient(103, "Shanika De Silva", 56, "0708901234", "Kidney stones");

        // Past visits, entered as history for three of the patients.
        addVisit(findPatient(101), "2026-03-12", "Dr. Wijesinghe", "Migraine", "Pain relief");
        addVisit(findPatient(101), "2026-06-02", "Dr. Wijesinghe", "Migraine follow up", "Medication review");
        addVisit(findPatient(104), "2026-01-20", "Dr. Karunaratne", "Type 2 diabetes", "Insulin adjustment");
        addVisit(findPatient(104), "2026-05-18", "Dr. Karunaratne", "Diabetes review", "Diet plan");
        addVisit(findPatient(108), "2026-07-04", "Dr. Mendis", "Hypertension", "Blood pressure tablets");

        // Two treatments already completed, oldest pushed first so the newest is on top.
        recordTreatmentDirectly(findPatient(102), "Dr. Fonseka", "Arm cast applied");
        recordTreatmentDirectly(findPatient(103), "Dr. Mendis", "Ultrasound scan");

        // Three patients currently waiting in the emergency unit, in arrival order.
        admitToEmergency(findPatient(105), "Severe chest pain");
        admitToEmergency(findPatient(110), "Motorbike accident");
        admitToEmergency(findPatient(107), "Breathing difficulty");
    }

    // ------------------------------------------------------------------
    // ID generation
    // ------------------------------------------------------------------

    private String nextCaseId() {
        return String.format("EC-%03d", nextCaseNumber++);
    }

    private String nextRecordId() {
        return String.format("TR-%03d", nextRecordNumber++);
    }

    private String nextVisitId() {
        return String.format("V-%03d", nextVisitNumber++);
    }

    // ------------------------------------------------------------------
    // Accessors used by the test suite
    // ------------------------------------------------------------------

    public PatientBST getPatientRecords() {
        return patientRecords;
    }

    public EmergencyQueue getEmergencyQueue() {
        return emergencyQueue;
    }

    public TreatmentStack getTreatmentHistory() {
        return treatmentHistory;
    }

    public VisitLinkedList getVisitHistoryOf(int patientId) {
        Patient patient = patientRecords.search(patientId);
        return patient == null ? null : patient.getVisitHistory();
    }
}
