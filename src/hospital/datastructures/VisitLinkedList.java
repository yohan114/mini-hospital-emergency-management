package hospital.datastructures;

import hospital.model.Visit;

/**
 * Singly linked list of the previous hospital visits of one patient.
 *
 * <p>Every {@code Patient} owns one instance of this list, so the visit history grows and
 * shrinks independently for each patient. New visits are appended at the tail, which keeps
 * the list in chronological order (oldest visit first). A {@code tail} reference is kept so
 * that appending is O(1) instead of walking the whole list every time.</p>
 *
 * <p>Each node holds a visit and a single {@code next} link - there is no {@code previous}
 * link, which is what makes the list <em>singly</em> linked.</p>
 */
public class VisitLinkedList {

    private static class Node {
        Visit data;
        Node next;

        Node(Visit data) {
            this.data = data;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    /**
     * Appends a visit to the end of the history. O(1).
     *
     * @return {@code false} when the Visit ID is already present in this history
     */
    public boolean addVisit(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Cannot add a null visit.");
        }
        if (searchVisit(visit.getVisitId()) != null) {
            return false;
        }
        Node node = new Node(visit);
        if (isEmpty()) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
        size++;
        return true;
    }

    /**
     * Inserts a visit at the front of the history. Used when an older visit is entered
     * after newer ones have already been recorded.
     */
    public boolean addVisitFirst(Visit visit) {
        if (visit == null) {
            throw new IllegalArgumentException("Cannot add a null visit.");
        }
        if (searchVisit(visit.getVisitId()) != null) {
            return false;
        }
        Node node = new Node(visit);
        node.next = head;
        head = node;
        if (tail == null) {
            tail = node;
        }
        size++;
        return true;
    }

    /**
     * Linear search through the chain of nodes. O(n) - unlike the BST there is no
     * ordering to exploit, but a visit history is short so this is acceptable.
     *
     * @return the matching visit, or {@code null} when the Visit ID is not in the history
     */
    public Visit searchVisit(String visitId) {
        Node current = head;
        while (current != null) {
            if (current.data.getVisitId().equalsIgnoreCase(visitId)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    /**
     * Removes a visit by ID by re-linking the previous node past the removed one.
     *
     * @return the removed visit, or {@code null} when the Visit ID was not found
     */
    public Visit removeVisit(String visitId) {
        Node current = head;
        Node previous = null;

        while (current != null) {
            if (current.data.getVisitId().equalsIgnoreCase(visitId)) {
                if (previous == null) {
                    head = current.next;       // removing the first node
                } else {
                    previous.next = current.next;
                }
                if (current == tail) {
                    tail = previous;           // removing the last node
                }
                size--;
                return current.data;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    /** The most recent visit (last node), or {@code null} when there is no history. */
    public Visit getLatestVisit() {
        return tail == null ? null : tail.data;
    }

    /** Prints the visit history, oldest visit first. */
    public void display() {
        if (isEmpty()) {
            System.out.println("  No visits recorded for this patient yet.");
            return;
        }
        System.out.println(Visit.tableHeader());
        Node current = head;
        while (current != null) {
            System.out.println(current.data.toTableRow());
            current = current.next;
        }
        System.out.println(Visit.TABLE_LINE);
        System.out.println("  Total visits: " + size);
    }

    /** Copies the visits into an array, index 0 being the oldest visit. */
    public Visit[] toArray() {
        Visit[] result = new Visit[size];
        Node current = head;
        int index = 0;
        while (current != null) {
            result[index++] = current.data;
            current = current.next;
        }
        return result;
    }
}
