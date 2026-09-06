# Video narration - what to say, in simple English

Short sentences, simple words, easy to say out loud. Read it a few times, then say it in
your own words. Do not say any line you cannot explain if someone asks you about it.

Timings are a guide. Total is about 9 minutes.

For how to set up OBS and run the paced demo, see [video-script.md](video-script.md).

---

## 1. Introduction - 30 seconds (your face must be visible)

> Hello. My name is Yohan Udara. My student ID is 2296.
>
> This is my mid assignment for CIT300, Data Structures and Algorithms.
>
> My project is called the Mini Hospital Emergency Management System. I wrote it in Java.
>
> Let me show you how it works.

---

## 2. Explanation of the system - 45 seconds

> This system manages patients in a small hospital.
>
> First we register a patient. The system saves their details.
>
> When a patient comes to the emergency unit, they join a waiting line. The doctor treats
> the first patient in the line.
>
> After the treatment, the system saves two things. One is a treatment record. The other is
> a visit in that patient's history.
>
> I used four data structures. Each one does a different job.
>
> A Binary Search Tree keeps all the patient records.
> A Queue keeps the emergency waiting line.
> A Stack keeps the treatment history.
> A Singly Linked List keeps the visit history of each patient.
>
> I wrote all four myself. I did not use ArrayList, LinkedList, Stack, or any other
> ready-made class from Java. I only used nodes and references.

---

## 3. GitHub repository and commit history - 45 seconds

*Open the repository page in the browser.*

> This is my GitHub repository.
>
> This is the README file. It explains my design. It also shows the time complexity of
> every operation.
>
> These are screenshots of my program running. There are 31 of them in the docs folder.

*Now click on Commits.*

> I did not upload my project in one commit. I committed my work step by step.
>
> First I created the project structure. Then I added the model classes.
>
> After that I built the Binary Search Tree. Then the queue, then the stack, then the
> linked list.
>
> Then I added the service layer, the menu, the tests, and the documentation.
>
> You can see the full history here.

---

## 4. How each data structure is used - 90 seconds

*Open the four classes in your editor, one at a time.*

**PatientBST.java**

> First, the Binary Search Tree. This is my PatientBST class.
>
> I use the Patient ID as the key.
>
> Every node follows one rule. All the IDs on the left are smaller. All the IDs on the
> right are bigger.
>
> Because of this rule, searching is fast. It is O(log n).
>
> And when I do an in-order traversal, the patients come out sorted by ID. I do not need
> any sorting code.

**EmergencyQueue.java**

> Second, the Queue. This is my EmergencyQueue class.
>
> I keep two references. One is called front and one is called rear.
>
> New patients join at the rear. The doctor takes patients from the front.
>
> This is FIFO. First In, First Out. The patient who comes first is treated first.
>
> Because I keep a rear reference, adding and removing are both O(1).

**TreatmentStack.java**

> Third, the Stack. This is my TreatmentStack class.
>
> Only the top reference moves.
>
> When a treatment is finished, I push the record on the top. When I pop, I get the newest
> record first.
>
> This is LIFO. Last In, First Out. Push and pop are both O(1).

**VisitLinkedList.java**

> Fourth, the Singly Linked List. This is my VisitLinkedList class.
>
> Every patient has their own list.
>
> Each node holds one visit and one next link. There is no previous link. That is why we
> call it singly linked.
>
> I add new visits at the end. So the history stays in date order.

---

## 5 and 6. The system running, and the operations - about 4 minutes

*Start with option 6, Load Sample Data.*

> Now let me run the system. First I load the sample data. It gives me eight patients,
> three patients waiting, and some past records.

### Binary Search Tree - about 60 seconds

*Menu 1, then option 4.*

> This is the in-order traversal. I added these patients in a mixed order. 105 first, then
> 102, then 108, and so on. But they come out sorted by ID. The tree does the sorting for
> me.

*Option 5.*

> This is the tree itself, drawn on its side. 105 is the root. Everything above it is the
> right side. Everything below it is the left side.

*Option 1, register a patient.*

> Now I add a new patient. The system prints the path the new node followed to reach its
> place.

*Option 2, search 103.*

> Now I search for patient 103. Look at this line. It made only four comparisons. There are
> eight patients, but it did not check all of them.

*Option 2 again, search 999.*

> And if the ID does not exist, the system says so. It does not crash.

*Option 3, delete 102.*

> Now I delete patient 102. This one is important. Patient 102 has two children below it.
> So the system replaces it with the next bigger ID, which is 104.

*Option 4 again.*

> And the list is still in the correct order. The tree is still correct after the deletion.

### Queue - about 60 seconds

*Menu 2, option 4.*

> This is the emergency waiting line. Position one is the front. This patient will be
> treated next.

*Option 3.*

> This is peek. It shows the next patient, but it does not remove him. He is still in the
> queue.

*Option 1, add patient 104.*

> Now a new patient arrives. He joins at position four, at the back. He cannot jump the
> line.

*Option 2, treat the next patient.*

> Now the doctor treats the next patient. Please look at these three lines.
>
> One. The patient is removed from the front of the queue.
> Two. A visit is added to his linked list.
> Three. A record is pushed onto the stack.
>
> So one action changes three data structures at the same time.

*Treat until the queue is empty, then try again.*

> Now the queue is empty. When I try to treat another patient, the system gives a clear
> message. It does not crash.

### Stack - about 45 seconds

*Menu 3, option 1.*

> This is the treatment history. Level one is the newest record.

*Option 4, push a record.*

> I add a new record. It goes straight to the top.

*Option 3, pop.*

> Now I pop. The newest record comes back first. That is Last In, First Out.
>
> And you can see this message. When I remove the record, the system also removes the visit
> that this treatment created.

*Pop until empty, then try again.*

> When the stack is empty, it also gives a clear message.

### Singly Linked List - about 45 seconds

*Menu 4, option 4, patient 101.*

> This is the visit history of one patient. The oldest visit is first.

*Option 1, add a visit.*

> I add a new visit. It goes to the end of the list.

*Option 3, search V-002, then V-999.*

> Now I search for a visit. The list checks the nodes one by one. This is a linear search,
> so it is O(n). The tree is faster, but a patient has only a few visits, so this is fine.
>
> And if the visit ID does not exist, it says so.

*Option 2, remove a visit.*

> Now I remove a visit. The node before it now points to the node after it. So the list
> closes the gap.

### Tests - about 20 seconds

*Switch to the second terminal and run run-tests.bat.*

> I also wrote a test suite. It has 132 checks. It tests all four data structures, and it
> tests how they work together. All of them pass.

---

## 7. Design decisions - 60 seconds

*Pick four or five of these.*

> Now let me explain some of my design decisions.
>
> First, the Patient ID cannot be changed. In my code it is final. The tree is sorted by
> this ID. If someone changes it, the search will break. So my update option can change the
> name, the age, the contact number and the condition. But never the ID.
>
> Second, when the queue or the stack is empty, my methods throw an exception. I did not
> return null. If I return null, the programmer can forget to check it. But an exception
> cannot be ignored. My menu catches it and prints a simple message.
>
> Third, my queue keeps a rear reference, and my linked list keeps a tail reference.
> Without them, adding at the end would be O(n). With them, it is O(1).
>
> Fourth, every treatment record saves the ID of the visit it created. Because of this,
> when I pop a record, I can also remove that visit. So the undo removes everything.
>
> Fifth, when I delete a patient, the system also removes him from the queue. But it keeps
> his old treatment records. The stack is the hospital's log, so it must stay complete.

---

## 8. What I learned - 30 seconds (face visible again)

> From this assignment I learned that the data structure must match the way we use the
> data.
>
> A tree is good when we search by a key. A queue is good when the order of arrival is
> important. A stack is good when we need an undo. And a list is good for the history of
> one patient.
>
> I also learned the weakness of my own tree. My BST is not balanced. If the patient IDs
> are added in order, like 101, 102, 103, then the tree becomes one long chain. Then the
> search becomes O(n), not O(log n).
>
> An AVL tree would solve this problem. That is what I would like to try next.
>
> Thank you for watching.

---

## Words to practise

These come up often, so say them a few times before recording:

* traversal (tra-VER-sal)
* in-order traversal
* enqueue (en-QUEUE) and dequeue (de-QUEUE)
* successor (suc-CES-sor)
* O(log n) - say it as "order log n"
* O(1) - say it as "order one"
* O(n) - say it as "order n"
* FIFO - say it as "fye-fo", or just say "First In, First Out"
* LIFO - say it as "lye-fo", or just say "Last In, First Out"
