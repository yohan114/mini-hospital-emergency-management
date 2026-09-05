@echo off
REM ===================================================================
REM  Mini Hospital Emergency Management System - build and run (Windows)
REM  Usage:  run.bat
REM ===================================================================
setlocal

if not exist out mkdir out

echo [1/2] Compiling sources...
dir /s /b src\*.java > out\sources.txt
javac -d out @out\sources.txt
if errorlevel 1 (
    echo.
    echo Compilation FAILED. Please fix the errors above.
    exit /b 1
)

echo [2/2] Starting application...
echo.
java -cp out hospital.app.Main

endlocal
