# Demonstration video - recording guide and narration

The video has **not** been recorded. It needs your face in the introduction and your voice
on the narration, so it has to be you. Everything else is set up here so the recording
itself is close to effortless.

Target length: **5 - 10 minutes**.

---

## Before you press record

**1. Check everything still works**

```
run-tests.bat
```

You should see `132 passed, 0 failed`. Leave that result on screen, it is used later.

**2. Set up OBS** (already installed at `C:\Program Files\obs-studio`)

* Sources: **Display Capture** for the screen, **Video Capture Device** for the webcam,
  **Audio Input Capture** for the microphone.
* Put the webcam in a corner as a small box. It only has to be visible during the
  introduction, but leaving it up the whole time is fine.
* Settings -> Output -> Recording Quality: *High Quality*, format **mp4**.
* Do a 20 second test recording first and play it back. Check that your voice is clear and
  that the console text is readable - if it is not, increase the terminal font size
  (Ctrl and + in Windows Terminal) rather than the recording resolution.

**3. Have these open and ready**

| Window                  | Showing                                                      |
|-------------------------|--------------------------------------------------------------|
| Browser                 | <https://github.com/yohan114/mini-hospital-emergency-management> |
| Editor                  | The four data structure classes, in tabs                     |
| Terminal 1              | In the project folder, ready to run the demo                 |
| Terminal 2              | The test result from step 1                                  |

---

## Let the demo drive itself

Instead of typing menu options live (slow, and easy to fumble on camera), play a demo
script at talking pace and narrate over it:

```bash
./docs/record-demo.sh docs/demo-scripts/01-bst-operations.txt 2
```

The second argument is the seconds between inputs. `2` is a good talking pace, `2.5` if you
want more room.

**Timing budget** (the marked values were measured, the rest are calculated from them):

| Script                     | Shows                                    | at `2`      | at `2.5`    |
|----------------------------|------------------------------------------|-------------|-------------|
| `01-bst-operations.txt`    | BST insert, search, delete, traversal    | 56s *(measured)* | 70s *(measured)* |
| `02-emergency-queue.txt`   | Queue enqueue, dequeue, peek, empty      | ~60s        | ~75s        |
| `03-treatment-stack.txt`   | Stack push, pop, peek, empty             | 46s *(measured)* | ~57s   |
| `04-visit-history.txt`     | Linked list add, search, remove          | ~56s        | ~70s        |
| `05-full-workflow.txt`     | All four together, and undo              | ~71s        | ~88s        |
| **01 to 04 together**      | **every operation**                      | **~3.6 min**| **~4.5 min**|

Which pace to use:

* **`2`** leaves about 5 minutes for the talking sections, a total near 9 minutes. Use it
  if you are narrating continuously over the output.
* **`2.5`** gives the tables 25% longer on screen, and totals about 9.5 minutes - inside
  the limit, but with only ~30 seconds of headroom. Use it if you want to pause and let a
  table sit there, and keep the talking sections tight.

The biggest single burst is about 20 lines - a full patient table plus the next menu - so
that is the moment the pace actually matters.

Script 05 is optional; the dequeue step in script 02 already shows all three structures
updating at once, so only add 05 if you are running short.

---

## Section by section

The narration lines below are talking points, not a teleprompter. Say them in your own
words - and if there is a point you could not defend if the marker paused the video and
asked about it, cut that line rather than reading it.

### 1. Introduction - 30 seconds (webcam on you)

> "Hi, I'm Yohan Udara, student ID 2296. This is my mid assignment for CIT300, Data
> Structures and Algorithms - a Mini Hospital Emergency Management System written in Java."

### 2. What the system does - 45 seconds

> "The system follows the flow of a small hospital. Patients are registered, they arrive at
> the emergency unit and wait their turn, a doctor treats whoever is at the front of the
> line, and each completed treatment is stored twice - as a treatment record, and as a
> visit in that patient's history.
>
> Each of those four jobs uses a different data structure, and all four are written from
> scratch - there is no ArrayList, LinkedList, Stack or TreeMap anywhere in the project,
> only nodes and references."

### 3. The repository - 45 seconds (browser)

Show the landing page, scroll the README, then click **Commits**.

> "The project is on GitHub. The README documents the design and the complexity of each
> operation, and the four screenshots here show one structure each - the full set of 23 is
> in the docs folder.
>
> The commit history shows it was built in stages: the models first, then the binary search
> tree, then the queue, the stack, the linked list, then the service layer that ties them
> together, then the menu, the tests and the documentation."

### 4. The four classes - 90 seconds (editor)

One point per class - open the file, scroll to the named method, say the line.

| Class             | Open              | Say                                                                                                                  |
|-------------------|-------------------|----------------------------------------------------------------------------------------------------------------------|
| `PatientBST`      | `insertRecursive` | "Keyed on Patient ID. Everything left of a node is smaller, everything right is larger, so search is O(log n) and an in-order traversal comes out sorted for free." |
| `EmergencyQueue`  | `enqueue`/`dequeue` | "Two references, front and rear. Arrivals attach at the rear, treatment takes from the front - that's FIFO, and keeping the rear reference makes both O(1)." |
| `TreatmentStack`  | `push`/`pop`      | "Only the top reference moves, so push and pop are O(1), and the newest record is the first one back - which is what makes undo work." |
| `VisitLinkedList` | `addVisit`        | "One list per patient. Each node holds a visit and a single next link. New visits append at the tail, so the history stays in date order." |

### 5. The program running - about 4 minutes (terminal)

Run each script with `record-demo.sh` and narrate the parts that matter.

**BST** - `./docs/record-demo.sh docs/demo-scripts/01-bst-operations.txt 2`

> * On the patient table: *"These were inserted in the order 105, 102, 108, 101 and so on,
>   but they come out sorted by ID. That is the in-order traversal - no sorting code."*
> * On the tree drawing: *"That's the actual tree on its side. 105 is the root, everything
>   above it is the right subtree, everything below is the left."*
> * On the search: *"Four comparisons to find patient 103 out of eight records, and the
>   program prints the path it took."*
> * On the delete of 102: *"102 had two children, so it is replaced by its in-order
>   successor, 104 - and the listing afterwards is still in order."*

**Queue** - `./docs/record-demo.sh docs/demo-scripts/02-emergency-queue.txt 2`

> * On the queue table: *"Front of the queue at position 1. Peek shows who's next without
>   removing them."*
> * On the enqueue: *"The new patient joins at position 4, at the rear. Nobody jumps the
>   line."*
> * On the dequeue - **the key moment, slow down here**: *"Treating the patient does three
>   things at once: the case leaves the front of the queue, a visit is appended to that
>   patient's linked list, and a record is pushed onto the stack. One action, three
>   structures."*
> * On the empty queue: *"And when nobody is waiting it reports it properly instead of
>   crashing - dequeue throws an exception that the menu catches."*

**Stack** - `./docs/record-demo.sh docs/demo-scripts/03-treatment-stack.txt 2`

> * *"Newest record at level 1. I push a record and it goes straight to the top; I pop and
>   that same record is the first one back. Last in, first out."*
> * On the empty stack: *"Same handling as the queue."*

**Linked list** - `./docs/record-demo.sh docs/demo-scripts/04-visit-history.txt 2`

> * *"This is one patient's history, oldest first. Adding appends at the tail. Searching
>   walks the next links one at a time - linear, because unlike the tree there is no
>   ordering to exploit."*
> * On the remove: *"Removing V-001 unlinks the head node and the list closes up behind
>   it."*

**Tests** - switch to Terminal 2

> *"And a test suite of 132 checks covering all four structures and the way they interact -
> including all three BST deletion cases, each one checking the traversal is still sorted
> afterwards."*

### 6. Design decisions - 60 seconds

Pick three or four. All are in the README under *Design decisions*.

> * *"The Patient ID is final. It is the key the tree is sorted on, so if it could be
>   edited the search would silently break - the update option deliberately doesn't offer
>   it."*
> * *"Dequeue, pop and peek throw an exception on an empty structure instead of returning
>   null. Null is easy to forget to check; an exception isn't. The menu catches it and
>   prints a plain message."*
> * *"The queue keeps a rear reference and the list keeps a tail reference. That turns two
>   O(n) operations into O(1)."*
> * *"Each treatment record stores the ID of the visit it created, which is what lets
>   popping a record undo the whole treatment step."*
> * *"Deleting a patient also removes them from the queue, but keeps their treatment
>   records - the stack is the audit trail."*

### 7. What you learned - 30 seconds (webcam)

> *"The main thing I took from this is that the structure should follow how the data is
> actually used - a tree for lookups by key, a queue for arrival order, a stack for undo,
> and a separate list per patient for history.
>
> The other thing is the limitation: my BST isn't balanced, so if patient IDs were inserted
> in ascending order it would degrade into a chain and search would drop to O(n). An AVL
> tree would fix that, and that's what I'd do next."*

---

## Checklist before uploading

- [ ] 5 - 10 minutes long
- [ ] Face visible during the introduction
- [ ] GitHub repository and commit history shown
- [ ] Each of the four structures explained
- [ ] The program demonstrated running
- [ ] BST, queue, stack and linked list operations all demonstrated
- [ ] Design decisions explained
- [ ] Reflection included
- [ ] Audio clear, console text readable on playback
- [ ] Video link added to the submission (and set so your marker can access it)
