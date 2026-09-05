#!/bin/sh
# ===================================================================
#  Runs the data structure test suite (Linux/macOS/Git Bash)
#  Usage:  ./run-tests.sh
# ===================================================================
set -e

mkdir -p out
find src -name "*.java" > out/sources.txt
javac -d out @out/sources.txt

java -cp out hospital.tests.DataStructureTests
