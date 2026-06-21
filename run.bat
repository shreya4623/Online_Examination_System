@echo off
setlocal

if not exist "bin\Main.class" (
    echo Project not compiled. Run compile.bat first.
    exit /b 1
)

set CP=bin;lib\mysql-connector-j-8.3.0.jar
java -cp %CP% ui.Main
