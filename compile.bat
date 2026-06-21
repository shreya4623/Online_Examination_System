@echo off
setlocal

if not exist "bin" mkdir bin

echo Compiling...
javac -encoding UTF-8 -d bin src\Main.java src\db\*.java src\model\*.java src\dao\*.java src\ui\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    exit /b 1
)

echo Compilation successful.
echo Run the application with: run.bat
