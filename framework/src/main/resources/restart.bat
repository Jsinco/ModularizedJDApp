@echo off

set PROJECTNAME=${PLACEHOLDER}
set VERSION=${PLACEHOLDER}

set JAR_NAME=%PROJECTNAME%-%VERSION%.jar

:: Find the Java process ID (PID)
for /f "tokens=2" %%i in ('jps -l ^| findstr %JAR_NAME%') do set PID=%%i

if "%PID%"=="" (
    echo No running process found for %JAR_NAME%.
) else (
    echo Killing process %PID%...
    taskkill /PID %PID% /F

    :: Wait for the process to exit
    :waitloop
    tasklist /FI "PID eq %PID%" 2>NUL | find /I "%PID%" >NUL
    if "%ERRORLEVEL%"=="0" (
        timeout /T 1 /NOBREAK >NUL
        goto waitloop
    )
)

echo Starting %JAR_NAME%...
start javaw -jar %JAR_NAME%