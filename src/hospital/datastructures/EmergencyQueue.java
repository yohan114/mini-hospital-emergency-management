package hospital.datastructures;

import hospital.model.EmergencyCase;

/**
 * Waiting line of the emergency unit, implemented as a linked queue.
 *
 * <p>FIFO: patients are added at the {@code rear} and always removed from the
 * {@code front}, so the patient who arrived first is treated first. Keeping a rear
 * reference makes both operations O(1) - no shifting and no capacity limit, which is why
 * a linked queue was chosen over an array based one.</p>
 */
public class EmergencyQueue {

    private static class Node {
        EmergencyCase data;
        Node next;

        Node(EmergencyCase data) {
            this.data = data;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public boolean isEmpty() {
        return front == null;
    }

    public int size() {
        return size;
    }

    /** Adds a patient to the end of the waiting line. O(1). */
    public void enqueue(EmergencyCase emergencyCase) {
        if (emergencyCase == null) {
            throw new IllegalArgumentException("Cannot enqueue a null emergency case.");
        }
        Node node = new Node(emergencyCase);
        if (isEmpty()) {
            front = node;
            rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    /**
     * Removes and returns the patient at the front of the line. O(1).
     *
     * @throws EmptyStructureException when nobody is waiting
     */
    public EmergencyCase dequeue() {
        if (isEmpty()) {
            throw new EmptyStructureException("The emergency queue is empty - there is no patient to treat.");
        }
        EmergencyCase removed = front.data;
        front = front.next;
        if (front == null) {
            rear = null; // the queue just became empty, drop the stale rear reference
        }
        size--;
        return removed;
    }

    /**
     * Returns the patient who will be treated next without removing them.
     *
     * @throws EmptyStructureException when nobody is waiting
     */
    public EmergencyCase peek() {
        if (isEmpty()) {
            throw new EmptyStructureException("The emergency queue is empty - nobody is waiting.");
        }
        return front.data;
    }

    /** Prints the waiting line from front to rear. */
    public void display() {
        if (isEmpty()) {
            System.out.println("  The emergency queue is empty - no patients are waiting.");
            return;
        }
        System.out.println(EmergencyCase.tableHeader());
        Node current = front;
        int position = 1;
        while (current != null) {
            System.out.println(current.data.toTableRow(position));
            current = current.next;
            position++;
        }
        System.out.println(EmergencyCase.TABLE_LINE);
        System.out.println("  Patients waiting: " + size + "   Next to be treated: " + front.data.getPatient().getName());
    }

    public boolean containsPatient(int patientId) {
        Node current = front;
        while (current != null) {
            if (current.data.getPatient().getPatientId() == patientId) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * Removes a waiting patient out of order, used when a patient leaves before being
     * seen or when the patient record itself is deleted. This is a maintenance operation,
     * the normal treatment flow always uses {@link #dequeue()}.
     *
     * @return the removed case, or {@code null} when the patient was not in the queue
     */
    public EmergencyCase removeByPatientId(int patientId) {
        Node current = front;
        Node previous = null;

        while (current != null) {
            if (current.data.getPatient().getPatientId() == patientId) {
                if (previous == null) {
                    front = current.next;      // removing the head
                } else {
                    previous.next = current.next;
                }
                if (current == rear) {
                    rear = previous;           // removing the tail
                }
                size--;
                return current.data;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }
}
