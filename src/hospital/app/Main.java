package hospital.app;

import hospital.datastructures.EmptyStructureException;
import hospital.model.EmergencyCase;
import hospital.model.Patient;
import hospital.model.TreatmentRecord;
import hospital.model.Visit;
import hospital.service.HospitalSystem;

import java.util.Scanner;

/**
 * Menu driven front end of the Mini Hospital Emergency Management System.
 *
 * <p>This class only collects input and prints results; every rule about how the data
 * structures are used lives in {@link HospitalSystem}.</p>
 *
 * <p>Run with the optional argument {@code --echo} to print back the lines that are read,
 * which is how the sample transcripts in {@code docs/sample-output} were produced.</p>
 */
public class Main {

    private static final String DOUBLE_LINE =
            "=================================================================================";
    private static final String SINGLE_LINE =
            "---------------------------------------------------------------------------------";

    private final HospitalSystem hospital = new HospitalSystem();
    private final ConsoleInput input;

    private Main(boolean echoInput) {
        this.input = new ConsoleInput(new Scanner(System.in));
        this.input.setEchoEnabled(echoInput);
    }

    public static void main(String[] args) {
        boolean echoInput = args.length > 0 && "--echo".equalsIgnoreCase(args[0]);
        new Main(echoInput).run();
    }

    private void run() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = input.readInt("  Enter your choice: ", 0, 6);
            switch (choice) {
                case 1:
                    patientRecordsMenu();
                    break;
                case 2:
                    emergencyUnitMenu();
                    break;
                case 3:
                    treatmentHistoryMenu();
                    break;
                case 4:
                    visitHistoryMenu();
                    break;
                case 5:
                    printHeader("SYSTEM SUMMARY");
                    hospital.displaySystemSummary();
                    break;
                case 6:
                    loadSampleData();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("  ! Unknown option.");
            }
        }
        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.println("  Thank you for using the Mini Hospital Emergency Management System.");
        System.out.println(DOUBLE_LINE);
    }

    // ==================================================================
    // 1. Patient records - Binary Search Tree
    // ==================================================================

    private void patientRecordsMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("PATIENT RECORDS  (Binary Search Tree)");
            System.out.println("  1. Register a new patient        (BST insert)");
            System.out.println("  2. Search for a patient by ID    (BST search)");
            System.out.println("  3. Delete a patient record       (BST delete)");
            System.out.println("  4. Display all patients          (in-order traversal)");
            System.out.println("  5. Display the tree structure");
            System.out.println("  6. Update patient details");
            System.out.println("  0. Back to main menu");
            System.out.println(SINGLE_LINE);

            int choice = input.readInt("  Enter your choice: ", 0, 6);
            System.out.println();
            switch (choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    searchPatient();
                    break;
                case 3:
                    deletePatient();
                    break;
                case 4:
                    System.out.println("  All patient records in ascending order of Patient ID:");
                    System.out.println();
                    hospital.displayAllPatients();
                    break;
                case 5:
                    System.out.println("  Tree drawn on its side, the root is on the left:");
                    System.out.println();
                    hospital.displayTreeStructure();
                    break;
                case 6:
                    updatePatient();
                    break;
                case 0:
                    inMenu = false;
                    break;
                default:
                    System.out.println("  ! Unknown option.");
            }
        }
    }

    private void registerPatient() {
        System.out.println("  Register a new patient");
        System.out.println(SINGLE_LINE);
        int patientId = input.readInt("  Patient ID (number) : ", 1, 999999);

        if (hospital.findPatient(patientId) != null) {
            System.out.println();
            System.out.println("  ! Patient ID " + patientId + " is already used. IDs must be unique,");
            System.out.println("    so the BST rejected this insert.");
            return;
        }

        String name = input.readText("  Patient Name        : ");
        int age = input.readInt("  Age                 : ", 0, 130);
        String contact = input.readText("  Contact Number      : ");
        String condition = input.readText("  Medical Condition   : ");

        if (hospital.registerPatient(patientId, name, age, contact, condition)) {
            System.out.println();
            System.out.println("  > Patient registered and inserted into the BST.");
            System.out.println("    Insert path : " + hospital.describeSearchPath(patientId));
        } else {
            System.out.println();
            System.out.println("  ! The patient could not be registered.");
        }
    }

    private void searchPatient() {
        System.out.println("  Search for a patient");
        System.out.println(SINGLE_LINE);
        int patientId = input.readInt("  Patient ID : ", 1, 999999);

        System.out.println();
        System.out.println("  Comparisons made : " + hospital.describeSearchPath(patientId));

        Patient patient = hospital.findPatient(patientId);
        if (patient == null) {
            System.out.println("  ! No patient record found for ID " + patientId + ".");
            return;
        }
        System.out.println();
        System.out.println("  Patient found:");
        System.out.println(patient.toDetailedString());
        if (hospital.isWaitingInEmergencyQueue(patientId)) {
            System.out.println("  Status            : currently waiting in the emergency queue");
        }
    }

    private void deletePatient() {
        System.out.println("  Delete a patient record");
        System.out.println(SINGLE_LINE);
        int patientId = input.readInt("  Patient ID : ", 1, 999999);

        Patient patient = hospital.findPatient(patientId);
        if (patient == null) {
            System.out.println();
            System.out.println("  ! No patient record found for ID " + patientId + ".");
            return;
        }

        System.out.println();
        System.out.println(patient.toDetailedString());
        System.out.println();
        boolean wasWaiting = hospital.isWaitingInEmergencyQueue(patientId);
        if (wasWaiting) {
            System.out.println("  ! This patient is waiting in the emergency queue and will be removed from it too.");
        }
        if (!input.readYesNo("  Delete this record permanently? (y/n) : ")) {
            System.out.println("  > Deletion cancelled.");
            return;
        }

        if (hospital.deletePatient(patientId)) {
            System.out.println();
            System.out.println("  > Patient " + patientId + " deleted from the BST.");
            if (wasWaiting) {
                System.out.println("  > The patient was also removed from the emergency queue.");
            }
            System.out.println("  > Treatment records already in the history are kept as an audit trail.");
        } else {
            System.out.println("  ! Deletion failed.");
        }
    }

    private void updatePatient() {
        System.out.println("  Update patient details");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        System.out.println();
        System.out.println(patient.toDetailedString());
        System.out.println();
        System.out.println("  Press Enter to keep the current value.");
        patient.setName(input.readText("  Name              : ", patient.getName()));
        patient.setAge(readAgeOrKeep(patient.getAge()));
        patient.setContactNumber(input.readText("  Contact Number    : ", patient.getContactNumber()));
        patient.setMedicalCondition(input.readText("  Medical Condition : ", patient.getMedicalCondition()));

        System.out.println();
        System.out.println("  > Record updated. The Patient ID is never changed, because it is the BST key.");
        System.out.println(patient.toDetailedString());
    }

    private int readAgeOrKeep(int currentAge) {
        while (true) {
            String value = input.readText("  Age               : ", String.valueOf(currentAge));
            try {
                int age = Integer.parseInt(value);
                if (age >= 0 && age <= 130) {
                    return age;
                }
                System.out.println("  ! Please enter an age between 0 and 130.");
            } catch (NumberFormatException ex) {
                System.out.println("  ! '" + value + "' is not a valid number.");
            }
        }
    }

    // ==================================================================
    // 2. Emergency unit - Queue
    // ==================================================================

    private void emergencyUnitMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("EMERGENCY UNIT  (Queue - First In, First Out)");
            System.out.println("  1. Add a patient to the queue    (enqueue)");
            System.out.println("  2. Treat the next patient        (dequeue)");
            System.out.println("  3. View the next patient         (peek)");
            System.out.println("  4. Display the waiting queue");
            System.out.println("  5. Remove a waiting patient      (patient left)");
            System.out.println("  0. Back to main menu");
            System.out.println(SINGLE_LINE);

            int choice = input.readInt("  Enter your choice: ", 0, 5);
            System.out.println();
            switch (choice) {
                case 1:
                    admitToEmergency();
                    break;
                case 2:
                    treatNextPatient();
                    break;
                case 3:
                    viewNextPatient();
                    break;
                case 4:
                    System.out.println("  Patients currently waiting, front of the queue first:");
                    System.out.println();
                    hospital.displayEmergencyQueue();
                    break;
                case 5:
                    cancelEmergencyCase();
                    break;
                case 0:
                    inMenu = false;
                    break;
                default:
                    System.out.println("  ! Unknown option.");
            }
        }
    }

    private void admitToEmergency() {
        System.out.println("  Add a patient to the emergency queue");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        if (hospital.isWaitingInEmergencyQueue(patient.getPatientId())) {
            System.out.println();
            System.out.println("  ! " + patient.getName() + " is already waiting in the queue.");
            return;
        }
        String reason = input.readText("  Reason for emergency : ");
        EmergencyCase admitted = hospital.admitToEmergency(patient, reason);

        System.out.println();
        System.out.println("  > " + admitted.getCaseId() + " added to the rear of the queue.");
        System.out.println("    Patient : " + patient.getName() + " (ID " + patient.getPatientId() + ")");
        System.out.println("    Arrived : " + admitted.getFormattedArrivalTime());
        System.out.println();
        hospital.displayEmergencyQueue();
    }

    private void treatNextPatient() {
        System.out.println("  Treat the next patient");
        System.out.println(SINGLE_LINE);

        EmergencyCase next;
        try {
            next = hospital.viewNextPatient();
        } catch (EmptyStructureException ex) {
            System.out.println("  ! " + ex.getMessage());
            return;
        }

        System.out.println("  Front of the queue : " + next.getCaseId() + " - " + next.getPatient().getName()
                + " (" + next.getReason() + ")");
        System.out.println();
        String doctor = input.readText("  Doctor Name      : ");
        String diagnosis = input.readText("  Diagnosis        : ");
        String treatment = input.readText("  Treatment Given  : ");

        try {
            TreatmentRecord record = hospital.treatNextPatient(doctor, diagnosis, treatment);
            System.out.println();
            System.out.println("  > " + next.getCaseId() + " removed from the front of the queue (dequeue).");
            System.out.println("  > Visit " + record.getVisitId() + " added to the visit history of "
                    + record.getPatientName() + " (linked list).");
            System.out.println("  > Record " + record.getRecordId() + " pushed onto the treatment history (stack).");
            System.out.println();
            hospital.displayEmergencyQueue();
        } catch (EmptyStructureException ex) {
            System.out.println("  ! " + ex.getMessage());
        }
    }

    private void viewNextPatient() {
        System.out.println("  Next patient to be treated");
        System.out.println(SINGLE_LINE);
        try {
            EmergencyCase next = hospital.viewNextPatient();
            System.out.println("  Case ID   : " + next.getCaseId());
            System.out.println("  Patient   : " + next.getPatient().getName()
                    + " (ID " + next.getPatient().getPatientId() + ")");
            System.out.println("  Reason    : " + next.getReason());
            System.out.println("  Arrived   : " + next.getFormattedArrivalTime());
            System.out.println();
            System.out.println("  The patient stays in the queue - peek only looks at the front.");
        } catch (EmptyStructureException ex) {
            System.out.println("  ! " + ex.getMessage());
        }
    }

    private void cancelEmergencyCase() {
        System.out.println("  Remove a waiting patient from the queue");
        System.out.println(SINGLE_LINE);
        int patientId = input.readInt("  Patient ID : ", 1, 999999);
        EmergencyCase removed = hospital.cancelEmergencyCase(patientId);

        System.out.println();
        if (removed == null) {
            System.out.println("  ! Patient " + patientId + " is not in the emergency queue.");
            return;
        }
        System.out.println("  > " + removed.getCaseId() + " (" + removed.getPatient().getName()
                + ") was removed from the queue without treatment.");
        System.out.println();
        hospital.displayEmergencyQueue();
    }

    // ==================================================================
    // 3. Treatment history - Stack
    // ==================================================================

    private void treatmentHistoryMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("TREATMENT HISTORY  (Stack - Last In, First Out)");
            System.out.println("  1. Display treatment records     (top of the stack first)");
            System.out.println("  2. View the most recent record   (peek)");
            System.out.println("  3. Remove the most recent record (pop)");
            System.out.println("  4. Add a treatment record        (push)");
            System.out.println("  0. Back to main menu");
            System.out.println(SINGLE_LINE);

            int choice = input.readInt("  Enter your choice: ", 0, 4);
            System.out.println();
            switch (choice) {
                case 1:
                    System.out.println("  Completed treatments, newest first:");
                    System.out.println();
                    hospital.displayTreatmentHistory();
                    break;
                case 2:
                    peekTreatment();
                    break;
                case 3:
                    popTreatment();
                    break;
                case 4:
                    pushTreatment();
                    break;
                case 0:
                    inMenu = false;
                    break;
                default:
                    System.out.println("  ! Unknown option.");
            }
        }
    }

    private void peekTreatment() {
        System.out.println("  Most recent treatment record");
        System.out.println(SINGLE_LINE);
        try {
            TreatmentRecord record = hospital.viewLastTreatment();
            printTreatmentRecord(record);
            System.out.println();
            System.out.println("  The record stays on the stack - peek only reads the top.");
        } catch (EmptyStructureException ex) {
            System.out.println("  ! " + ex.getMessage());
        }
    }

    private void popTreatment() {
        System.out.println("  Remove the most recent treatment record");
        System.out.println(SINGLE_LINE);

        TreatmentRecord top;
        try {
            top = hospital.viewLastTreatment();
        } catch (EmptyStructureException ex) {
            System.out.println("  ! The treatment history is empty - there is nothing to remove.");
            return;
        }

        printTreatmentRecord(top);
        System.out.println();
        if (top.getVisitId() != null) {
            System.out.println("  ! Visit " + top.getVisitId()
                    + " will also be removed from the patient's visit history.");
        }
        if (!input.readYesNo("  Remove this record? (y/n) : ")) {
            System.out.println("  > Cancelled, the stack was not changed.");
            return;
        }

        TreatmentRecord removed = hospital.undoLastTreatment();
        System.out.println();
        System.out.println("  > " + removed.getRecordId() + " popped from the treatment history.");
        if (removed.getVisitId() != null) {
            System.out.println("  > Visit " + removed.getVisitId() + " removed from the linked list of "
                    + removed.getPatientName() + ".");
        }
    }

    private void pushTreatment() {
        System.out.println("  Add a treatment record directly");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        String doctor = input.readText("  Doctor Name     : ");
        String treatment = input.readText("  Treatment Given : ");
        TreatmentRecord record = hospital.recordTreatmentDirectly(patient, doctor, treatment);

        System.out.println();
        System.out.println("  > " + record.getRecordId() + " pushed onto the treatment history stack.");
        System.out.println("    It is now the top of the stack, so it will be the first record popped.");
    }

    private void printTreatmentRecord(TreatmentRecord record) {
        System.out.println("  Record ID       : " + record.getRecordId());
        System.out.println("  Patient         : " + record.getPatientName() + " (ID " + record.getPatientId() + ")");
        System.out.println("  Doctor          : " + record.getDoctorName());
        System.out.println("  Treatment Given : " + record.getTreatmentGiven());
        System.out.println("  Completed At    : " + record.getFormattedCompletedAt());
        System.out.println("  Linked Visit    : " + (record.getVisitId() == null ? "-" : record.getVisitId()));
    }

    // ==================================================================
    // 4. Patient visit history - Singly Linked List
    // ==================================================================

    private void visitHistoryMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printHeader("PATIENT VISIT HISTORY  (Singly Linked List)");
            System.out.println("  1. Add a visit to a patient      (insert at the end)");
            System.out.println("  2. Remove a visit                (unlink a node)");
            System.out.println("  3. Search for a visit            (linear search)");
            System.out.println("  4. Display a patient's visits    (traverse the list)");
            System.out.println("  0. Back to main menu");
            System.out.println(SINGLE_LINE);

            int choice = input.readInt("  Enter your choice: ", 0, 4);
            System.out.println();
            switch (choice) {
                case 1:
                    addVisit();
                    break;
                case 2:
                    removeVisit();
                    break;
                case 3:
                    searchVisit();
                    break;
                case 4:
                    displayVisits();
                    break;
                case 0:
                    inMenu = false;
                    break;
                default:
                    System.out.println("  ! Unknown option.");
            }
        }
    }

    private void addVisit() {
        System.out.println("  Add a visit to a patient's history");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        String date = input.readText("  Visit Date (yyyy-mm-dd) : ");
        String doctor = input.readText("  Doctor Name             : ");
        String diagnosis = input.readText("  Diagnosis               : ");
        String treatment = input.readText("  Treatment               : ");

        Visit visit = hospital.addVisit(patient, date, doctor, diagnosis, treatment);
        System.out.println();
        System.out.println("  > Visit " + visit.getVisitId() + " appended to the end of the list for "
                + patient.getName() + ".");
        System.out.println();
        hospital.displayVisitHistory(patient);
    }

    private void removeVisit() {
        System.out.println("  Remove a visit from a patient's history");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        if (patient.getVisitHistory().isEmpty()) {
            System.out.println();
            System.out.println("  ! " + patient.getName() + " has no recorded visits.");
            return;
        }
        System.out.println();
        hospital.displayVisitHistory(patient);
        System.out.println();

        String visitId = input.readText("  Visit ID to remove : ");
        Visit removed = hospital.removeVisit(patient, visitId);
        System.out.println();
        if (removed == null) {
            System.out.println("  ! Visit " + visitId + " was not found in this patient's history.");
            return;
        }
        System.out.println("  > Visit " + removed.getVisitId() + " (" + removed.getDiagnosis()
                + ") removed. The previous node now points past it.");
        System.out.println();
        hospital.displayVisitHistory(patient);
    }

    private void searchVisit() {
        System.out.println("  Search for a visit");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        String visitId = input.readText("  Visit ID : ");
        Visit visit = hospital.searchVisit(patient, visitId);

        System.out.println();
        if (visit == null) {
            System.out.println("  ! Visit " + visitId + " was not found for " + patient.getName() + ".");
            return;
        }
        System.out.println("  Visit found:");
        System.out.println("    Visit ID   : " + visit.getVisitId());
        System.out.println("    Date       : " + visit.getVisitDate());
        System.out.println("    Doctor     : " + visit.getDoctorName());
        System.out.println("    Diagnosis  : " + visit.getDiagnosis());
        System.out.println("    Treatment  : " + visit.getTreatment());
    }

    private void displayVisits() {
        System.out.println("  Display a patient's visit history");
        System.out.println(SINGLE_LINE);
        Patient patient = promptForPatient();
        if (patient == null) {
            return;
        }
        System.out.println();
        System.out.println("  Visit history of " + patient.getName() + " (ID " + patient.getPatientId()
                + "), oldest visit first:");
        System.out.println();
        hospital.displayVisitHistory(patient);
    }

    // ==================================================================
    // Shared helpers
    // ==================================================================

    /** Asks for a Patient ID and looks it up in the BST. Prints a message when not found. */
    private Patient promptForPatient() {
        int patientId = input.readInt("  Patient ID : ", 1, 999999);
        Patient patient = hospital.findPatient(patientId);
        if (patient == null) {
            System.out.println();
            System.out.println("  ! No patient record found for ID " + patientId + ".");
            System.out.println("    Register the patient first from the Patient Records menu.");
            return null;
        }
        System.out.println("  Patient    : " + patient.getName() + " (" + patient.getMedicalCondition() + ")");
        return patient;
    }

    private void loadSampleData() {
        printHeader("LOAD SAMPLE DATA");
        if (hospital.isSampleDataLoaded()) {
            System.out.println("  ! The sample data has already been loaded.");
            return;
        }
        hospital.loadSampleData();
        System.out.println("  > Sample hospital data loaded:");
        System.out.println("      8 patient records inserted into the BST");
        System.out.println("      5 past visits added to three linked lists");
        System.out.println("      2 completed treatments pushed onto the stack");
        System.out.println("      3 patients waiting in the emergency queue");
        System.out.println();
        hospital.displaySystemSummary();
    }

    private void printBanner() {
        System.out.println(DOUBLE_LINE);
        System.out.println("        MINI HOSPITAL EMERGENCY MANAGEMENT SYSTEM");
        System.out.println("        CIT300 - Data Structures and Algorithms");
        System.out.println(DOUBLE_LINE);
        System.out.println("   Patient records ......... Binary Search Tree");
        System.out.println("   Emergency unit .......... Queue  (First In, First Out)");
        System.out.println("   Treatment history ....... Stack  (Last In, First Out)");
        System.out.println("   Patient visit history ... Singly Linked List");
        System.out.println(DOUBLE_LINE);
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println(SINGLE_LINE);
        System.out.println("  MAIN MENU");
        System.out.println(SINGLE_LINE);
        System.out.println("  1. Patient Records          (Binary Search Tree)");
        System.out.println("  2. Emergency Unit           (Queue - FIFO)");
        System.out.println("  3. Treatment History        (Stack - LIFO)");
        System.out.println("  4. Patient Visit History    (Singly Linked List)");
        System.out.println("  5. System Summary");
        System.out.println("  6. Load Sample Data");
        System.out.println("  0. Exit");
        System.out.println(SINGLE_LINE);
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println(DOUBLE_LINE);
        System.out.println("  " + title);
        System.out.println(DOUBLE_LINE);
    }
}
