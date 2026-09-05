# Demonstration video plan

The video itself has **not** been recorded yet. This file is the plan to follow when
recording it, mapped to the eight points the assignment asks for.

Target length: **5 - 10 minutes**. Record the screen with the webcam on for the
introduction (the assignment requires the face to be visible there).

Before recording:

```
run-tests.bat        make sure all 132 checks pass
run.bat              make sure the program starts
```

Keep the project open in the editor, a terminal ready, and the GitHub repository page open
in a browser.

---

## 1. Introduction - about 30 seconds (face visible)

Name, student ID, course (CIT300 - Data Structures and Algorithms), and the title of the
assignment: Mini Hospital Emergency Management System.

## 2. What the system does - about 45 seconds

Explain the flow in plain words: patients are registered, they arrive at the emergency
unit and wait their turn, a doctor treats the next patient in line, and every completed
treatment is stored both as a treatment record and as an entry in that patient's visit
history. Mention that all four structures are written from scratch, without any
`java.util` collection.

## 3. GitHub repository and commit history - about 45 seconds

Show the repository page, the folder structure, and then the commit list. Scroll through
the commits from the first one to the last and point out that the project was built in
steps: models, then the BST, then the queue, the stack, the linked list, the service
layer, the menu, the tests and the documentation.

## 4. How each data structure is used - about 90 seconds

Open the four classes in the editor and explain one thing about each:

| Structure          | Class             | The one point to make                                                      |
|--------------------|-------------------|----------------------------------------------------------------------------|
| Binary Search Tree | `PatientBST`      | Keyed on Patient ID, so search is O(log n) and the in-order traversal is already sorted |
| Queue              | `EmergencyQueue`  | `front` and `rear` references, enqueue at the rear and dequeue at the front, so FIFO and both O(1) |
| Stack              | `TreatmentStack`  | Only the `top` reference moves, so push and pop are O(1) and the newest record comes back first |
| Singly Linked List | `VisitLinkedList` | One list per patient, each node has a single `next` link, appended at the tail |

## 5 and 6. The system running, with the key operations

Start the program and use **option 6 (Load Sample Data)** first. The demo scripts in
`docs/demo-scripts` follow exactly this order, so they can be used as a rehearsal.

**BST - about 60 seconds**

* Menu 1 -> 4: all patients, in ascending order of Patient ID, produced by the in-order
  traversal.
* Menu 1 -> 5: the tree drawn on its side; point at the root and at the left and right
  subtrees.
* Menu 1 -> 1: register a new patient, and show the insert path that is printed.
* Menu 1 -> 2: search for an existing ID, and point at the "Comparisons made" line - four
  comparisons instead of eight.
* Menu 1 -> 2: search for an ID that does not exist.
* Menu 1 -> 3: delete a patient with two children (for example 102), then 1 -> 4 again to
  show that the list is still in order.

**Queue - about 60 seconds**

* Menu 2 -> 4: the waiting line, front first.
* Menu 2 -> 3: peek - the patient stays in the queue.
* Menu 2 -> 1: add a patient to the rear.
* Menu 2 -> 2: treat the next patient, and read out the three lines the program prints -
  the case leaves the queue, a visit is added to the linked list, a record is pushed onto
  the stack.
* Drain the queue and try to treat again, to show the empty queue message.

**Stack - about 45 seconds**

* Menu 3 -> 1: the history, newest record first.
* Menu 3 -> 4: push a record, then display again to show it on top.
* Menu 3 -> 3: pop, and mention that the linked visit is removed with it.
* Pop until it is empty, then pop again to show the empty stack message.

**Linked list - about 45 seconds**

* Menu 4 -> 4 for patient 101: the visit history.
* Menu 4 -> 1: add a visit, appended at the end.
* Menu 4 -> 3: search for a Visit ID, then for one that does not exist.
* Menu 4 -> 2: remove a visit, then display again.

**Tests - about 20 seconds**

Run `run-tests.bat` and show the final line: 132 passed, 0 failed.

## 7. Implementation and design decisions - about 60 seconds

Pick three or four of these (they are all in the README under *Design decisions*):

* The Patient ID is `final` because it is the BST key; changing it would break the search.
* `dequeue`, `pop` and `peek` throw `EmptyStructureException` instead of returning `null`,
  so the empty case cannot be ignored; the menu catches it and prints a message.
* The queue keeps a `rear` reference and the list keeps a `tail` reference, which turns two
  O(n) operations into O(1).
* A treatment record stores the ID of the visit it created, which is what makes the
  pop operation able to undo the whole treatment step.
* Deleting a patient also removes them from the emergency queue, but keeps their treatment
  records as an audit trail.

## 8. What was learned - about 30 seconds

For example: choosing the structure that fits the access pattern (a tree for lookups by
key, a queue for arrival order, a stack for undo, a list per patient for history); why an
unbalanced BST can degrade to O(n) and how an AVL tree would fix it; and how much easier
the deletion cases were to get right with tests that check the traversal after every
delete.

---

## Checklist before uploading

- [ ] 5 - 10 minutes long
- [ ] Face visible during the introduction
- [ ] GitHub repository and commit history shown
- [ ] Each of the four structures explained
- [ ] The program is demonstrated running
- [ ] BST, queue, stack and linked list operations all demonstrated
- [ ] Design decisions explained
- [ ] Reflection included
- [ ] Audio is clear and the console text is readable at the recorded resolution
