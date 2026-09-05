#!/bin/sh
# ===================================================================
#  Mini Hospital Emergency Management System - build and run (Linux/macOS/Git Bash)
#  Usage:  ./run.sh
# ===================================================================
set -e

mkdir -p out

echo "[1/2] Compiling sources..."
find src -name "*.java" > out/sources.txt
javac -d out @out/sources.txt

echo "[2/2] Starting application..."
echo
java -cp out hospital.app.Main
