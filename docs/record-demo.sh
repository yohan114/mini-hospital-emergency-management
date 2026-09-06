#!/bin/sh
# ===================================================================
#  Plays a demo script into the program at a readable, paced speed.
#
#  Use this while screen recording: the demo drives itself at talking
#  pace, so you can narrate instead of typing and hunting for keys.
#
#  Usage (from the project root):
#      ./docs/record-demo.sh                                   full workflow, 2.5s per step
#      ./docs/record-demo.sh docs/demo-scripts/01-bst-operations.txt
#      ./docs/record-demo.sh docs/demo-scripts/01-bst-operations.txt 4
#
#  Argument 1: the input script (default: the full workflow)
#  Argument 2: seconds to wait between inputs (default: 2.5)
# ===================================================================
set -e

SCRIPT="${1:-docs/demo-scripts/05-full-workflow.txt}"
DELAY="${2:-2.5}"

if [ ! -f "$SCRIPT" ]; then
    echo "Input script not found: $SCRIPT"
    exit 1
fi

if [ ! -d out ]; then
    echo "Compiling first..."
    mkdir -p out
    find src -name "*.java" > out/sources.txt
    javac -d out @out/sources.txt
fi

# Feed the script one line at a time with a pause between each, so the output
# appears at the speed a viewer can actually read it.
{
    sleep 2
    while IFS= read -r line; do
        printf '%s\n' "$line"
        sleep "$DELAY"
    done < "$SCRIPT"
    sleep 3
} | java -cp out hospital.app.Main --echo
