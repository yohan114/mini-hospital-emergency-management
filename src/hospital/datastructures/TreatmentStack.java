package hospital.datastructures;

import hospital.model.TreatmentRecord;

/**
 * History of completed treatments, implemented as a linked stack.
 *
 * <p>LIFO: every completed treatment is pushed on the {@code top}, and {@code pop}
 * removes the most recent one. That matches how the hospital reviews its work - the last
 * treatment is the one most likely to be re-checked or corrected - and it makes "undo the
 * last treatment" a single O(1) operation.</p>
 */
public class TreatmentStack {

    private static class Node {
        TreatmentRecord data;
        Node next;

        Node(TreatmentRecord data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node top;
    private int size;

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /** Adds a completed treatment record on top of the history. O(1). */
    public void push(TreatmentRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Cannot push a null treatment record.");
        }
        top = new Node(record, top);
        size++;
    }

    /**
     * Removes and returns the most recently completed treatment record. O(1).
     *
     * @throws EmptyStructureException when no treatment has been recorded yet
     */
    public TreatmentRecord pop() {
        if (isEmpty()) {
            throw new EmptyStructureException("The treatment history is empty - there is nothing to remove.");
        }
        TreatmentRecord removed = top.data;
        top = top.next;
        size--;
        return removed;
    }

    /**
     * Returns the most recent record without removing it.
     *
     * @throws EmptyStructureException when no treatment has been recorded yet
     */
    public TreatmentRecord peek() {
        if (isEmpty()) {
            throw new EmptyStructureException("The treatment history is empty - there is nothing to show.");
        }
        return top.data;
    }

    /** Prints the history from the top of the stack (newest) down to the bottom (oldest). */
    public void display() {
        if (isEmpty()) {
            System.out.println("  The treatment history is empty - no treatment has been completed yet.");
            return;
        }
        System.out.println(TreatmentRecord.tableHeader());
        Node current = top;
        int level = 1;
        while (current != null) {
            System.out.println(current.data.toTableRow(level));
            current = current.next;
            level++;
        }
        System.out.println(TreatmentRecord.TABLE_LINE);
        System.out.println("  Records in history: " + size + "   Most recent: " + top.data.getRecordId());
    }

    /** Number of records stored for one patient, without changing the stack. */
    public int countForPatient(int patientId) {
        int count = 0;
        Node current = top;
        while (current != null) {
            if (current.data.getPatientId() == patientId) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    /** Copies the records into an array, index 0 being the top of the stack. */
    public TreatmentRecord[] toArray() {
        TreatmentRecord[] result = new TreatmentRecord[size];
        Node current = top;
        int index = 0;
        while (current != null) {
            result[index++] = current.data;
            current = current.next;
        }
        return result;
    }
}
