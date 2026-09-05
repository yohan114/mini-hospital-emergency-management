@echo off
REM ===================================================================
REM  Runs the data structure test suite (Windows)
REM  Usage:  run-tests.bat
REM ===================================================================
setlocal

if not exist out mkdir out

dir /s /b src\*.java > out\sources.txt
javac -d out @out\sources.txt
if errorlevel 1 (
    echo.
    echo Compilation FAILED.
    exit /b 1
)

java -cp out hospital.tests.DataStructureTests

endlocal
