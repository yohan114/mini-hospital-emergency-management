# Screenshots of the program output

All 31 screenshots of the Mini Hospital Emergency Management System, grouped by the data
structure they demonstrate. Four of them also appear on the
[main README](../../README.md#screenshots).

The text in every image is the real, unedited output of the program. Each one was produced
by feeding an input file from [`../demo-scripts`](../demo-scripts) into the menu and
rendering the resulting transcript, which is kept in full in
[`../sample-output`](../sample-output):

```
java -cp out hospital.app.Main --echo < docs/demo-scripts/01-bst-operations.txt
```

The rendering step is [`../make-screenshots.py`](../make-screenshots.py).

---

## Startup

**1. Banner, main menu and the sample data being loaded.** The summary at the bottom shows
the size of all four structures at once.

![Startup and sample data](01-startup-and-sample-data.png)

---

## Binary Search Tree - patient records

**2. In-order traversal.** The patients were inserted in the order 105, 102, 108, 101,
104, 107, 110, 103, and come back sorted by Patient ID with no sorting step.

![In-order traversal](02-bst-inorder-traversal.png)

**3. The tree itself**, drawn on its side with the root on the left margin.

![Tree structure](03-bst-tree-structure.png)

**4. Insert.** The path the new node took down the tree is printed.

![Insert a patient](04-bst-insert-patient.png)

**5. Search, hit and miss.** The "Comparisons made" line shows four comparisons instead of
scanning all eight records.

![BST search](05-bst-search.png)

**6. Delete a node with two children.** Patient 102 has 101 and 104 below it.

![BST delete](06-bst-delete.png)

**7. After the deletions.** 104 was promoted into 102's place as the in-order successor,
and the traversal is still perfectly ordered.

![Records after deletion](07-bst-after-delete.png)

---

## Queue - emergency unit (FIFO)

**8. The waiting line**, front of the queue first, and peek - which leaves the patient in
place.

![Queue display and peek](08-queue-display-and-peek.png)

**9. Enqueue.** The new case joins at position 4, the rear, never jumping the line.

![Enqueue](09-queue-enqueue.png)

**10. Dequeue.** Treating the next patient removes them from the front and updates three
structures in one step.

![Dequeue](10-queue-dequeue.png)

**11. Empty queue handling.** Both dequeue and peek report cleanly instead of crashing.

![Empty queue handling](11-queue-empty-handling.png)

---

## Stack - treatment history (LIFO)

**12. The history**, newest record at level 1.

![Stack display](12-stack-display.png)

**13. Push.** The new record becomes the top of the stack.

![Push](13-stack-push.png)

**14. Pop.** The newest record comes back first, and the visit it created is removed with
it.

![Pop](14-stack-pop.png)

**15. Empty stack handling.**

![Empty stack handling](15-stack-empty-handling.png)

---

## Singly Linked List - patient visit history

**16. One patient's visit history**, oldest visit first.

![Display visits](16-list-display-visits.png)

**17. Add a visit.** Appended at the tail, so the history stays chronological.

![Add a visit](17-list-add-visit.png)

**18. Search, hit and miss.** A linear walk along the `next` links.

![Search a visit](18-list-search-visit.png)

**19. Remove a visit.** The head node is unlinked and the list closes up behind it.

![Remove a visit](19-list-remove-visit.png)

---

## All four structures together

**20. Treating a patient** dequeues the case, appends a visit to that patient's linked
list and pushes a record onto the stack.

![Treatment across all structures](20-integration-treatment.png)

**21. Undo.** Popping the record also removes the visit it created, undoing the whole step.

![Undo a treatment](21-integration-undo.png)

**22. System summary** - the size of every structure, the BST height and the ID range.

![System summary](22-system-summary.png)

---

## Tests

**23. The test suite** - 132 checks, 0 failures.

![Test suite result](23-test-suite-result.png)

---

## Validation and error handling

These come from the feature sweep
([`../demo-scripts/07-all-features.txt`](../demo-scripts/07-all-features.txt)), which
drives every menu option and every error path in a single run - including the ones the
four demo scripts above never reach.

**24. Cold start.** Before any data is loaded, every structure reports its empty state
instead of failing - empty tree, empty queue, empty stack, and an invalid menu choice.

![Cold start on an empty system](24-cold-start-empty-system.png)

**25. Input validation.** Text where a number is expected, numbers out of range, and a
blank required field. Each one re-prompts rather than crashing.

![Input validation](25-input-validation.png)

**26. Duplicate Patient ID.** The BST rejects the insert, because an ID must identify
exactly one patient.

![Duplicate Patient ID rejected](26-duplicate-patient-id.png)

**27. Update patient details.** Pressing Enter keeps the current value, typing replaces it.
The Patient ID is never offered for editing because it is the key the tree is sorted on.

![Update patient details](27-update-patient.png)

**28. Deletion declined.** Answering `n` leaves the record untouched.

![Delete declined](28-delete-declined.png)

**29. Deleting a queued patient.** The record leaves the BST *and* the emergency queue, so
the queue can never hold a case whose patient no longer exists. Treatment records are kept
as an audit trail.

![Delete also clears the queue](29-delete-clears-queue.png)

**30. Duplicate enqueue.** A patient already waiting cannot be added to the queue twice.

![Duplicate enqueue blocked](30-duplicate-enqueue.png)

**31. Pop declined.** Answering `n` leaves the stack unchanged, still holding all three
records.

![Pop declined](31-pop-declined.png)
