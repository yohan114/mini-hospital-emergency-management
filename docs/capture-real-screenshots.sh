#!/bin/sh
# ===================================================================
#  Takes real screenshots of the program running in a console window.
#
#  For each shot it writes a short input sequence, opens a real Windows
#  console window running the program with that input, photographs only
#  that window, and lets the window close itself.
#
#  These are genuine screen captures of the running system, not images
#  generated from the saved text output.
#
#  Usage (from the project root, on Windows):
#      ./docs/capture-real-screenshots.sh
# ===================================================================
set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
WIN_ROOT="$(cd "$ROOT" && pwd -W 2>/dev/null | sed 's|/|\\|g')"
OUT_DIR="$ROOT/docs/screenshots"
INPUT_FILE="$ROOT/shot-input.tmp"
LAUNCHER="$ROOT/shot-launch.tmp.bat"

mkdir -p "$OUT_DIR"

# The window title is passed in as %1 so every shot gets a unique one. Windows Terminal
# keeps all its windows in one process, so the title is the only reliable way to tell them
# apart, and a title left over from a previous shot would mean photographing the wrong one.
cat > "$LAUNCHER" <<EOF
@echo off
title %~1
mode con: cols=122 lines=52
cd /d "$WIN_ROOT"
java -cp out hospital.app.Main --echo < "shot-input.tmp"
echo.
rem ping is used as the delay: Git Bash puts its own timeout.exe ahead of the Windows one
ping -n 8 127.0.0.1 > nul
EOF

# capture <output name> <input line> <input line> ...
capture() {
    name="$1"
    shift
    : > "$INPUT_FILE"
    for line in "$@"; do
        printf '%s\n' "$line" >> "$INPUT_FILE"
    done

    title="HospitalDemo-$name"
    MSYS_NO_PATHCONV=1 cmd.exe /c start "" "$WIN_ROOT\\shot-launch.tmp.bat" "$title" > /dev/null 2>&1
    sleep 4
    MSYS_NO_PATHCONV=1 powershell.exe -ExecutionPolicy Bypass -File "$WIN_ROOT\\docs\\capture-window.ps1" \
        -Title "$title" -Out "$WIN_ROOT\\docs\\screenshots\\$name.png" 2>&1 | sed 's/^/  /'
    # Wait for this window to close before the next one opens, so titles never overlap.
    sleep 8
}

#            name                          menu inputs that end on the frame to photograph
capture "01-startup-and-sample-data"  "6"
capture "02-bst-inorder-traversal"    "6" "1" "4"
capture "03-bst-tree-structure"       "6" "1" "5"
capture "04-bst-insert-patient"       "6" "1" "1" "106" "Tharindu Silva" "34" "0759988776" "Dengue fever"
capture "05-bst-search"               "6" "1" "2" "103"
capture "06-bst-delete"               "6" "1" "3" "102" "y"
capture "08-queue-display-and-peek"   "6" "2" "4"
capture "10-queue-dequeue"            "6" "2" "2" "Dr. Silva" "Acute chest pain" "ECG and cardiac medication"
capture "11-queue-empty-handling"     "2" "2"
capture "12-stack-display"            "6" "3" "1"
capture "14-stack-pop"                "6" "3" "3" "y"
capture "15-stack-empty-handling"     "3" "3"
capture "16-list-display-visits"      "6" "4" "4" "101"
capture "19-list-remove-visit"        "6" "4" "2" "101" "V-001"
capture "25-input-validation"         "6" "1" "1" "abc" "0" "106" "" "Tharindu Silva" "abc" "200" "34" "0759988776" "Dengue fever"
capture "27-update-patient"           "6" "1" "6" "101" "" "29" "" "Chronic migraine"

capture "07-bst-after-delete"         "6" "1" "3" "103" "y" "3" "102" "y" "4"
capture "09-queue-enqueue"            "6" "2" "1" "104" "Severe hypoglycaemia"
capture "13-stack-push"               "6" "3" "4" "105" "Dr. Silva" "Cardiac monitoring"
capture "17-list-add-visit"           "6" "4" "1" "101" "2026-09-01" "Dr. Wijesinghe" "Chronic migraine" "MRI scan requested"
capture "18-list-search-visit"        "6" "4" "3" "101" "V-002"
capture "22-system-summary"           "6" "5"
capture "24-cold-start-empty-system"  "abc" "9" "5"
capture "26-duplicate-patient-id"     "6" "1" "1" "105"
capture "28-delete-declined"          "6" "1" "3" "105" "n"
capture "29-delete-clears-queue"      "6" "1" "3" "110" "y"
capture "30-duplicate-enqueue"        "6" "2" "1" "105"
capture "31-pop-declined"             "6" "3" "3" "n"
capture "20-integration-treatment"    "1" "1" "201" "Chamodi Weerasinghe" "27" "0761122334" "Appendicitis" "0" \
                                      "2" "1" "201" "Severe abdominal pain" "2" "Dr. Fernando" "Acute appendicitis" "Emergency appendectomy"
capture "21-integration-undo"         "1" "1" "201" "Chamodi Weerasinghe" "27" "0761122334" "Appendicitis" "0" \
                                      "2" "1" "201" "Severe abdominal pain" "2" "Dr. Fernando" "Acute appendicitis" "Emergency appendectomy" \
                                      "0" "3" "3" "y"

# The test suite is a different main class, so it needs its own launcher.
cat > "$LAUNCHER" <<EOF
@echo off
title %~1
mode con: cols=122 lines=52
cd /d "$WIN_ROOT"
java -cp out hospital.tests.DataStructureTests
echo.
ping -n 8 127.0.0.1 > nul
EOF
capture "23-test-suite-result"

rm -f "$INPUT_FILE" "$LAUNCHER"
echo
echo "Real screenshots written to docs/screenshots"
