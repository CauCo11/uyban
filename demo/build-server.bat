@echo off
echo Building Server Application...

REM Clean old server files
if exist server rmdir /s /q server
if exist ServerApp.jar del ServerApp.jar
if exist ServerApp.exe del ServerApp.exe

REM Compile server files
echo Compiling server files...
javac -d . src\server\*.java
if %errorlevel% neq 0 (
    echo ❌ Server compilation failed!
    pause
    exit /b 1
)

REM Create server JAR
echo Creating ServerApp.jar...
jar cfm ServerApp.jar manifest-server.txt server\*.class
if %errorlevel% neq 0 (
    echo ❌ Server JAR creation failed!
    pause
    exit /b 1
)

REM Build EXE if Launch4j is available
if exist "C:\Program Files (x86)\Launch4j\launch4jc.exe" (
    echo Building ServerApp.exe...
    "C:\Program Files (x86)\Launch4j\launch4jc.exe" launch4j-server-config.xml
    if %errorlevel% eq 0 (
        echo ✅ ServerApp.exe created successfully!
    )
) else (
    echo Launch4j not found. Only JAR file created.
)

echo ✅ Server build completed!
pause
