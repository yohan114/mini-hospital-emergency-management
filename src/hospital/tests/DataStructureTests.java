package hospital.tests;

import hospital.datastructures.EmergencyQueue;
import hospital.datastructures.EmptyStructureException;
import hospital.datastructures.PatientBST;
import hospital.datastructures.TreatmentStack;
import hospital.datastructures.VisitLinkedList;
import hospital.model.EmergencyCase;
import hospital.model.Patient;
import hospital.model.TreatmentRecord;
import hospital.model.Visit;
import hospital.service.HospitalSystem;

/**
 * Test suite for the four data structures and for the way the service layer combines
 * them. It is written as a plain {@code main} method with a very small check helper, so
 * that it runs with nothing but the JDK - no external test library is needed.
 *
 * <pre>
 *   javac -d out (all sources)
 *   java -cp out hospital.tests.DataStructureTests
 * </pre>
 *
 * The process exits with code 1 if any check fails, so it can also be used in a script.
 */
public class DataStructureTests {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  DATA STRUCTURE TEST SUITE");
        System.out.println("=================================================================");

        testBinarySearchTree();
        testEmergencyQueue();
        testTreatmentStack();
        testVisitLinkedList();
        testSystemIntegration();

        System.out.println();
        System.out.println("=================================================================");
        System.out.println("  RESULT: " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " checks in total");
        System.out.println("=================================================================");
        System.exit(failed == 0 ? 0 : 1);
    }

    // ------------------------------------------------------------------
    // 1. Binary Search Tree
    // ------------------------------------------------------------------

    private static void testBinarySearchTree() {
        section("Binary Search Tree - patient records");

        PatientBST tree = new PatientBST();
        check("a new tree is empty", tree.isEmpty());
        equals("an empty tree has size 0", 0, tree.size());
        equals("an empty tree has height -1", -1, tree.height());
        check("search on an empty tree returns null", tree.search(101) == null);

        // Insert order chosen to build a branched tree, not a straight chain.
        int[] ids = { 105, 102, 108, 101, 104, 107, 110, 103 };
        for (int id : ids) {
            tree.insert(patient(id));
        }
        equals("all 8 patients were inserted", 8, tree.size());
        check("the tree is no longer empty", !tree.isEmpty());
        equals("the height of the sample tree is 3", 3, tree.height());

        check("a duplicate ID is rejected", !tree.insert(patient(105)));
        equals("the size did not change after the duplicate", 8, tree.size());
        check("a null patient is rejected", !tree.insert(null));

        check("search finds the root", tree.search(105) != null);
        check("search finds a leaf", tree.search(103) != null);
        equals("search returns the right record", "Patient 110", tree.search(110).getName());
        check("search returns null for a missing ID", tree.search(999) == null);
        check("contains is true for an existing ID", tree.contains(101));
        check("contains is false for a missing ID", !tree.contains(999));

        equals("the search path follows the BST rule", "105 -> 102 -> 104 -> 103", tree.searchPath(103));

        equals("findMin returns the smallest ID", 101, tree.findMin().getPatientId());
        equals("findMax returns the largest ID", 110, tree.findMax().getPatientId());
        equals("in-order traversal is sorted", "101,102,103,104,105,107,108,110", idsInOrder(tree));

        // Case 1: leaf deletion.
        check("deleting a leaf succeeds", tree.delete(103));
        equals("the size dropped by one", 7, tree.size());
        check("the deleted leaf can no longer be found", tree.search(103) == null);
        equals("the order is still correct", "101,102,104,105,107,108,110", idsInOrder(tree));

        // Case 2: node with a single child (108 keeps 107 and 110, so delete 110 first).
        tree.delete(110);
        check("deleting a node with one child succeeds", tree.delete(108));
        check("the parent now points at the surviving child", tree.contains(107));
        equals("the order survives a one-child deletion", "101,102,104,105,107", idsInOrder(tree));

        // Case 3: node with two children - the root 105 has 102 and 107 below it.
        check("deleting a node with two children succeeds", tree.delete(105));
        check("the deleted root is gone", tree.search(105) == null);
        equals("the in-order successor took its place", "101,102,104,107", idsInOrder(tree));
        equals("the size is correct after three deletions", 4, tree.size());

        check("deleting a missing ID returns false", !tree.delete(999));
        equals("a failed delete does not change the size", 4, tree.size());

        equals("toSortedArray returns every patient", 4, tree.toSortedArray().length);
    }

    // ------------------------------------------------------------------
    // 2. Queue
    // ------------------------------------------------------------------

    private static void testEmergencyQueue() {
        section("Queue - emergency unit (FIFO)");

        EmergencyQueue queue = new EmergencyQueue();
        check("a new queue is empty", queue.isEmpty());
        equals("an empty queue has size 0", 0, queue.size());
        throwsEmpty("dequeue on an empty queue throws", queue::dequeue);
        throwsEmpty("peek on an empty queue throws", queue::peek);

        queue.enqueue(emergencyCase("EC-001", 101));
        queue.enqueue(emergencyCase("EC-002", 102));
        queue.enqueue(emergencyCase("EC-003", 103));
        equals("three patients are waiting", 3, queue.size());
        check("the queue is not empty", !queue.isEmpty());

        equals("peek shows the first patient who arrived", "EC-001", queue.peek().getCaseId());
        equals("peek does not remove anybody", 3, queue.size());

        equals("the first dequeue returns the first arrival", "EC-001", queue.dequeue().getCaseId());
        equals("the second dequeue returns the second arrival", "EC-002", queue.dequeue().getCaseId());
        equals("one patient is left", 1, queue.size());

        check("containsPatient finds a waiting patient", queue.containsPatient(103));
        check("containsPatient is false for a treated patient", !queue.containsPatient(101));

        equals("the last dequeue returns the last arrival", "EC-003", queue.dequeue().getCaseId());
        check("the queue is empty again", queue.isEmpty());
        throwsEmpty("dequeue throws once the queue is drained", queue::dequeue);

        // Enqueueing after the queue emptied must still work: the rear reference has to
        // have been cleared, otherwise the new node would be linked behind a dead node.
        queue.enqueue(emergencyCase("EC-004", 104));
        equals("the queue can be reused after being emptied", "EC-004", queue.peek().getCaseId());
        equals("the size is correct after reuse", 1, queue.size());

        // Removing a patient who leaves before being seen.
        queue.enqueue(emergencyCase("EC-005", 105));
        queue.enqueue(emergencyCase("EC-006", 106));
        check("removing the middle patient works", queue.removeByPatientId(105) != null);
        equals("the size dropped after the removal", 2, queue.size());
        check("removing the front patient works", queue.removeByPatientId(104) != null);
        equals("the front moved on", "EC-006", queue.peek().getCaseId());
        check("removing the rear patient works", queue.removeByPatientId(106) != null);
        check("the queue is empty after removing everybody", queue.isEmpty());
        check("removing a patient who is not waiting returns null", queue.removeByPatientId(999) == null);

        queue.enqueue(emergencyCase("EC-007", 107));
        equals("the rear reference was reset by removeByPatientId", "EC-007", queue.peek().getCaseId());
        equals("only one patient is waiting", 1, queue.size());
    }

    // ------------------------------------------------------------------
    // 3. Stack
    // ------------------------------------------------------------------

    private static void testTreatmentStack() {
        section("Stack - treatment history (LIFO)");

        TreatmentStack stack = new TreatmentStack();
        check("a new stack is empty", stack.isEmpty());
        equals("an empty stack has size 0", 0, stack.size());
        throwsEmpty("pop on an empty stack throws", stack::pop);
        throwsEmpty("peek on an empty stack throws", stack::peek);

        stack.push(record("TR-001", 101));
        stack.push(record("TR-002", 102));
        stack.push(record("TR-003", 101));
        equals("three records were pushed", 3, stack.size());

        equals("peek shows the newest record", "TR-003", stack.peek().getRecordId());
        equals("peek does not remove anything", 3, stack.size());

        equals("pop returns the newest record", "TR-003", stack.pop().getRecordId());
        equals("the next pop returns the one below it", "TR-002", stack.pop().getRecordId());
        equals("one record is left", 1, stack.size());

        equals("countForPatient counts only that patient", 1, stack.countForPatient(101));
        equals("countForPatient is 0 for an unknown patient", 0, stack.countForPatient(999));

        equals("the last pop returns the oldest record", "TR-001", stack.pop().getRecordId());
        check("the stack is empty again", stack.isEmpty());
        throwsEmpty("pop throws once the stack is drained", stack::pop);

        stack.push(record("TR-004", 104));
        stack.push(record("TR-005", 105));
        TreatmentRecord[] all = stack.toArray();
        equals("toArray has one entry per record", 2, all.length);
        equals("toArray starts at the top of the stack", "TR-005", all[0].getRecordId());
        equals("toArray ends at the bottom of the stack", "TR-004", all[1].getRecordId());
    }

    // ------------------------------------------------------------------
    // 4. Singly Linked List
    // ------------------------------------------------------------------

    private static void testVisitLinkedList() {
        section("Singly Linked List - patient visit history");

        VisitLinkedList list = new VisitLinkedList();
        check("a new list is empty", list.isEmpty());
        equals("an empty list has size 0", 0, list.size());
        check("searching an empty list returns null", list.searchVisit("V-001") == null);
        check("removing from an empty list returns null", list.removeVisit("V-001") == null);
        check("the latest visit of an empty list is null", list.getLatestVisit() == null);

        check("the first visit is added", list.addVisit(visit("V-001")));
        check("the second visit is added", list.addVisit(visit("V-002")));
        check("the third visit is added", list.addVisit(visit("V-003")));
        equals("all three visits are stored", 3, list.size());
        equals("visits stay in the order they were added", "V-001,V-002,V-003", visitIds(list));
        equals("the latest visit is the last one added", "V-003", list.getLatestVisit().getVisitId());

        check("a duplicate Visit ID is rejected", !list.addVisit(visit("V-002")));
        equals("the size did not change after the duplicate", 3, list.size());

        check("search finds an existing visit", list.searchVisit("V-002") != null);
        check("search is not case sensitive", list.searchVisit("v-002") != null);
        check("search returns null for a missing visit", list.searchVisit("V-999") == null);

        check("addVisitFirst puts an older visit in front", list.addVisitFirst(visit("V-000")));
        equals("the new visit is at the head", "V-000,V-001,V-002,V-003", visitIds(list));

        // Remove from the middle, then from the head, then from the tail.
        equals("removing a middle node returns it", "V-002", list.removeVisit("V-002").getVisitId());
        equals("the neighbours are relinked", "V-000,V-001,V-003", visitIds(list));
        equals("removing the head returns it", "V-000", list.removeVisit("V-000").getVisitId());
        equals("the head moved on", "V-001,V-003", visitIds(list));
        equals("removing the tail returns it", "V-003", list.removeVisit("V-003").getVisitId());
        equals("only the first visit is left", "V-001", visitIds(list));
        check("removing a missing visit returns null", list.removeVisit("V-999") == null);
        equals("the size is correct after the removals", 1, list.size());

        // Appending after the tail was removed proves the tail reference was updated.
        list.addVisit(visit("V-004"));
        equals("a visit can still be appended after a tail removal", "V-001,V-004", visitIds(list));
        equals("the latest visit is the newly appended one", "V-004", list.getLatestVisit().getVisitId());

        list.removeVisit("V-001");
        list.removeVisit("V-004");
        check("the list is empty after removing everything", list.isEmpty());
        check("the latest visit is null again", list.getLatestVisit() == null);
    }

    // ------------------------------------------------------------------
    // 5. The structures working together
    // ------------------------------------------------------------------

    private static void testSystemIntegration() {
        section("Integration - the four structures working together");

        HospitalSystem hospital = new HospitalSystem();
        check("a patient can be registered", hospital.registerPatient(201, "Test Patient", 30, "0770000000", "Fever"));
        check("a duplicate registration is rejected",
                !hospital.registerPatient(201, "Other Patient", 40, "0771111111", "Cough"));

        Patient patient = hospital.findPatient(201);
        check("the registered patient can be found", patient != null);
        equals("a new patient has no visits", 0, patient.getVisitHistory().size());

        hospital.admitToEmergency(patient, "High fever");
        check("the patient is waiting in the queue", hospital.isWaitingInEmergencyQueue(201));
        equals("one patient is in the queue", 1, hospital.getEmergencyQueue().size());

        TreatmentRecord record = hospital.treatNextPatient("Dr. Test", "Viral fever", "Paracetamol");
        check("the queue is empty after the treatment", hospital.isEmergencyQueueEmpty());
        equals("the treatment was pushed onto the stack", 1, hospital.getTreatmentHistory().size());
        equals("a visit was added to the linked list", 1, patient.getVisitHistory().size());
        check("the record points at the visit it created",
                patient.getVisitHistory().searchVisit(record.getVisitId()) != null);

        TreatmentRecord undone = hospital.undoLastTreatment();
        equals("undo pops the record that was pushed", record.getRecordId(), undone.getRecordId());
        check("the stack is empty after the undo", hospital.isTreatmentHistoryEmpty());
        equals("the linked visit was removed as well", 0, patient.getVisitHistory().size());

        // Deleting a patient must also clear the emergency queue entry.
        hospital.admitToEmergency(patient, "Second visit");
        check("the patient is waiting again", hospital.isWaitingInEmergencyQueue(201));
        check("the patient record is deleted", hospital.deletePatient(201));
        check("the deleted patient left the queue too", !hospital.isWaitingInEmergencyQueue(201));
        check("the patient can no longer be found", hospital.findPatient(201) == null);
        check("deleting a missing patient returns false", !hospital.deletePatient(201));

        HospitalSystem demo = new HospitalSystem();
        demo.loadSampleData();
        equals("the sample data registers 8 patients", 8, demo.getPatientRecords().size());
        equals("the sample data queues 3 patients", 3, demo.getEmergencyQueue().size());
        equals("the sample data pushes 2 treatment records", 2, demo.getTreatmentHistory().size());
        equals("the sample data adds 5 visits", 5, demo.countAllVisits());
        demo.loadSampleData();
        equals("loading the sample data twice does not duplicate it", 8, demo.getPatientRecords().size());
    }

    // ------------------------------------------------------------------
    // Tiny check helpers
    // ------------------------------------------------------------------

    private static void section(String title) {
        System.out.println();
        System.out.println("-- " + title + " " + dashes(60 - title.length()));
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }

    private static void equals(String description, Object expected, Object actual) {
        boolean same = expected == null ? actual == null : expected.equals(actual);
        if (same) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description + "  (expected <" + expected + "> but was <" + actual + ">)");
        }
    }

    /** Passes only when the action throws {@link EmptyStructureException}. */
    private static void throwsEmpty(String description, Runnable action) {
        try {
            action.run();
            failed++;
            System.out.println("  [FAIL] " + description + "  (no exception was thrown)");
        } catch (EmptyStructureException expected) {
            passed++;
            System.out.println("  [PASS] " + description);
        } catch (RuntimeException unexpected) {
            failed++;
            System.out.println("  [FAIL] " + description + "  (threw " + unexpected.getClass().getSimpleName() + ")");
        }
    }

    private static String dashes(int count) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < Math.max(3, count); i++) {
            line.append('-');
        }
        return line.toString();
    }

    // ------------------------------------------------------------------
    // Test data builders
    // ------------------------------------------------------------------

    private static Patient patient(int id) {
        return new Patient(id, "Patient " + id, 30, "0770000000", "Condition " + id);
    }

    private static EmergencyCase emergencyCase(String caseId, int patientId) {
        return new EmergencyCase(caseId, patient(patientId), "Reason " + caseId);
    }

    private static TreatmentRecord record(String recordId, int patientId) {
        return new TreatmentRecord(recordId, patientId, "Patient " + patientId,
                "Dr. Test", "Treatment " + recordId, null);
    }

    private static Visit visit(String visitId) {
        return new Visit(visitId, "2026-01-01", "Dr. Test", "Diagnosis " + visitId, "Treatment " + visitId);
    }

    /** Comma separated IDs produced by the in-order traversal. */
    private static String idsInOrder(PatientBST tree) {
        StringBuilder ids = new StringBuilder();
        for (Patient patient : tree.toSortedArray()) {
            if (ids.length() > 0) {
                ids.append(',');
            }
            ids.append(patient.getPatientId());
        }
        return ids.toString();
    }

    /** Comma separated Visit IDs in list order. */
    private static String visitIds(VisitLinkedList list) {
        StringBuilder ids = new StringBuilder();
        for (Visit visit : list.toArray()) {
            if (ids.length() > 0) {
                ids.append(',');
            }
            ids.append(visit.getVisitId());
        }
        return ids.toString();
    }
}
