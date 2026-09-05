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

    /**
     * Searches for a patient by ID.
     *
     * <p>The loop follows a single root-to-node path, so the work is proportional to the
     * height of the tree, O(log n) when the tree is balanced.</p>
     *
     * @return the matching patient, or {@code null} when the ID is not in the tree
     */
    public Patient search(int patientId) {
        Node current = root;
        while (current != null) {
            int currentId = current.patient.getPatientId();
            if (patientId == currentId) {
                return current.patient;
            }
            current = patientId < currentId ? current.left : current.right;
        }
        return null;
    }

    public boolean contains(int patientId) {
        return search(patientId) != null;
    }

    /**
     * Builds the sequence of IDs that a search visits, e.g. {@code "104 -> 102 -> 103"}.
     * Used by the menu to show how few comparisons a BST search needs.
     */
    public String searchPath(int patientId) {
        if (isEmpty()) {
            return "(tree is empty)";
        }
        StringBuilder path = new StringBuilder();
        Node current = root;
        while (current != null) {
            if (path.length() > 0) {
                path.append(" -> ");
            }
            path.append(current.patient.getPatientId());

            int currentId = current.patient.getPatientId();
            if (patientId == currentId) {
                return path.toString();
            }
            current = patientId < currentId ? current.left : current.right;
        }
        return path + " -> (not found)";
    }

    /**
     * Deletes the patient with the given ID.
     *
     * @return {@code true} if a record was removed, {@code false} if the ID was not found
     */
    public boolean delete(int patientId) {
        if (!contains(patientId)) {
            return false;
        }
        root = deleteRecursive(root, patientId);
        size--;
        return true;
    }

    /**
     * Standard BST deletion, three cases:
     * <ol>
     *   <li>leaf: simply detach it,</li>
     *   <li>one child: replace the node with that child,</li>
     *   <li>two children: copy the in-order successor (smallest ID in the right subtree)
     *       into this node, then delete that successor from the right subtree.</li>
     * </ol>
     */
    private Node deleteRecursive(Node current, int patientId) {
        if (current == null) {
            return null;
        }
        int currentId = current.patient.getPatientId();

        if (patientId < currentId) {
            current.left = deleteRecursive(current.left, patientId);
        } else if (patientId > currentId) {
            current.right = deleteRecursive(current.right, patientId);
        } else {
            // Case 1 and 2: no child, or exactly one child.
            if (current.left == null) {
                return current.right;
            }
            if (current.right == null) {
                return current.left;
            }
            // Case 3: two children.
            Node successor = findMinNode(current.right);
            current.patient = successor.patient;
            current.right = deleteRecursive(current.right, successor.patient.getPatientId());
        }
        return current;
    }

    private Node findMinNode(Node current) {
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    /** Patient with the smallest ID, or {@code null} when the tree is empty. */
    public Patient findMin() {
        return isEmpty() ? null : findMinNode(root).patient;
    }

    /** Patient with the largest ID, or {@code null} when the tree is empty. */
    public Patient findMax() {
        if (isEmpty()) {
            return null;
        }
        Node current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.patient;
    }

    /** Number of edges on the longest root-to-leaf path. An empty tree has height -1. */
    public int height() {
        return heightRecursive(root);
    }

    private int heightRecursive(Node current) {
        if (current == null) {
            return -1;
        }
        return 1 + Math.max(heightRecursive(current.left), heightRecursive(current.right));
    }

    /**
     * Prints the tree rotated 90 degrees to the left, so the root is on the left margin
     * and the largest ID appears at the top. Useful when explaining the structure.
     */
    public void displayTreeStructure() {
        if (isEmpty()) {
            System.out.println("  The tree is empty.");
            return;
        }
        printSideways(root, 0);
        System.out.println();
        System.out.println("  Nodes: " + size + "   Height: " + height());
    }

    private void printSideways(Node current, int depth) {
        if (current == null) {
            return;
        }
        printSideways(current.right, depth + 1);
        System.out.println(indent(depth) + "[" + current.patient.getPatientId() + "] "
                + current.patient.getName());
        printSideways(current.left, depth + 1);
    }

    /** Written as a loop instead of String.repeat so the project also builds on Java 8. */
    private static String indent(int depth) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            spaces.append("     ");
        }
        return spaces.toString();
    }

    /**
     * Copies every patient into an array in ascending ID order. The service layer uses
     * this when it has to loop over all records without exposing the node structure.
     */
    public Patient[] toSortedArray() {
        Patient[] result = new Patient[size];
        fillArray(root, result, new int[] { 0 });
        return result;
    }

    private void fillArray(Node current, Patient[] target, int[] index) {
        if (current == null) {
            return;
        }
        fillArray(current.left, target, index);
        target[index[0]++] = current.patient;
        fillArray(current.right, target, index);
    }
}
