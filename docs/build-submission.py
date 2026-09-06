"""
Builds the LMS submission package.

Produces CIT300_Mid_Assignment_2296_Yohan_Udara.zip in the project root, containing the
items the assignment brief asks to be submitted: the Java source code, the README, the
screenshots of the program output, and a cover note carrying the repository link.

Usage (from the project root):

    python docs/build-submission.py
"""

import os
import zipfile

STUDENT_NAME = "Yohan Udara"
STUDENT_ID = "2296"
REPO_URL = "https://github.com/yohan114/mini-hospital-emergency-management"

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
PACKAGE = "CIT300_Mid_Assignment_" + STUDENT_ID + "_" + STUDENT_NAME.replace(" ", "_")
ZIP_PATH = os.path.join(ROOT, PACKAGE + ".zip")

COVER_NOTE = """CIT300 - Data Structures and Algorithms
Individual Mid Assignment
Mini Hospital Emergency Management System

Student name : {name}
Student ID   : {sid}

GitHub repository : {repo}
Demonstration video : <paste the video link here before submitting>

--------------------------------------------------------------------------
WHAT IS IN THIS PACKAGE
--------------------------------------------------------------------------

README.md          Full documentation: design, complexity of every operation,
                   design decisions and the assignment checklist.

src/               Complete Java source code.
                     hospital/model/            Patient, Visit, EmergencyCase,
                                                TreatmentRecord
                     hospital/datastructures/   PatientBST, EmergencyQueue,
                                                TreatmentStack, VisitLinkedList
                     hospital/service/          HospitalSystem
                     hospital/app/              Main, ConsoleInput
                     hospital/tests/            DataStructureTests

screenshots/       23 screenshots of the program output, one for every
                   operation. See screenshots/README.md for the captions.

sample-output/     The full text transcripts the screenshots were taken from.

demo-scripts/      The input files that produce those transcripts.

run.bat / run.sh   Build and run the program.
run-tests.bat      Build and run the test suite (132 checks).

--------------------------------------------------------------------------
HOW TO BUILD AND RUN
--------------------------------------------------------------------------

Windows:
    run.bat

Linux / macOS / Git Bash:
    ./run.sh

Manually:
    javac -d out $(find src -name "*.java")
    java -cp out hospital.app.Main

Tests:
    run-tests.bat        (or ./run-tests.sh)

Requires a JDK. Developed on JDK 21, also compiles with --release 8.

--------------------------------------------------------------------------
DATA STRUCTURES
--------------------------------------------------------------------------

All four are implemented from scratch. No java.util collection class
(ArrayList, LinkedList, Queue, Stack, TreeMap and so on) is used anywhere in
the system - only nodes and references.

    Patient records        Binary Search Tree, keyed on Patient ID
    Emergency unit         Queue, FIFO
    Treatment history      Stack, LIFO
    Patient visit history  Singly Linked List, one per patient
""".format(name = STUDENT_NAME, sid = STUDENT_ID, repo = REPO_URL)

# (source path relative to the project root, destination inside the zip)
CONTENTS = [
    ("README.md", "README.md"),
    ("run.bat", "run.bat"),
    ("run.sh", "run.sh"),
    ("run-tests.bat", "run-tests.bat"),
    ("run-tests.sh", "run-tests.sh"),
    ("src", "src"),
    ("docs/screenshots", "screenshots"),
    ("docs/sample-output", "sample-output"),
    ("docs/demo-scripts", "demo-scripts"),
]


def add_path(archive, source, destination):
    """Adds a file, or every file under a directory, to the archive."""
    full = os.path.join(ROOT, source)
    if os.path.isfile(full):
        archive.write(full, PACKAGE + "/" + destination)
        return 1

    count = 0
    for folder, _, files in os.walk(full):
        for name in sorted(files):
            path = os.path.join(folder, name)
            relative = os.path.relpath(path, full).replace("\\", "/")
            archive.write(path, PACKAGE + "/" + destination + "/" + relative)
            count += 1
    return count


def main():
    if os.path.exists(ZIP_PATH):
        os.remove(ZIP_PATH)

    with zipfile.ZipFile(ZIP_PATH, "w", zipfile.ZIP_DEFLATED) as archive:
        archive.writestr(PACKAGE + "/SUBMISSION.txt", COVER_NOTE)
        print("  SUBMISSION.txt                    1 file")
        for source, destination in CONTENTS:
            count = add_path(archive, source, destination)
            print("  " + destination.ljust(34) + str(count) + (" file" if count == 1 else " files"))

    size = os.path.getsize(ZIP_PATH)
    print()
    print("  " + os.path.basename(ZIP_PATH))
    print("  " + str(round(size / 1024.0 / 1024.0, 2)) + " MB")


if __name__ == "__main__":
    main()
