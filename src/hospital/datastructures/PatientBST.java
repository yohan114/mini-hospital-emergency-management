package hospital.datastructures;

import hospital.model.Patient;

/**
 * Binary Search Tree of patient records, keyed on the Patient ID.
 *
 * <p>Ordering rule kept by every node:</p>
 * <pre>
 *     all IDs in the left subtree  &lt;  node ID  &lt;  all IDs in the right subtree
 * </pre>
 *
 * <p>Duplicate Patient IDs are rejected, because an ID must identify exactly one
 * patient record.</p>
 */
public class PatientBST {

    /** A node of the tree. Kept private so callers can only work with Patient objects. */
    private static class Node {
        Patient patient;
        Node left;
        Node right;

        Node(Patient patient) {
            this.patient = patient;
        }
    }

    private Node root;
    private int size;

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    /**
     * Inserts a new patient into the tree.
     *
     * @return {@code true} if inserted, {@code false} if the Patient ID already exists
     */
    public boolean insert(Patient patient) {
        if (patient == null) {
            return false;
        }
        int before = size;
        root = insertRecursive(root, patient);
        return size > before;
    }

    /**
     * Classic recursive insert: walk down comparing IDs and hang the new node on the
     * empty branch that is reached.
     */
    private Node insertRecursive(Node current, Patient patient) {
        if (current == null) {
            size++;
            return new Node(patient);
        }
        int id = patient.getPatientId();
        int currentId = current.patient.getPatientId();

        if (id < currentId) {
            current.left = insertRecursive(current.left, patient);
        } else if (id > currentId) {
            current.right = insertRecursive(current.right, patient);
        }
        // id == currentId -> duplicate, tree is left unchanged and size is not increased
        return current;
    }

    /**
     * In-order traversal (left, node, right). Because of the BST ordering rule this
     * visits the patients in ascending order of Patient ID.
     */
    public void displayInOrder() {
        if (isEmpty()) {
            System.out.println("  No patient records found. The tree is empty.");
            return;
        }
        System.out.println(Patient.tableHeader());
        inOrderRecursive(root);
        System.out.println(Patient.TABLE_LINE);
        System.out.println("  Total patients: " + size);
    }

    private void inOrderRecursive(Node current) {
        if (current == null) {
            return;
        }
        inOrderRecursive(current.left);
        System.out.println(current.patient.toTableRow());
        inOrderRecursive(current.right);
    }
}
