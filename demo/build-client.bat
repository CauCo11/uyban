@echo off
echo Building Client Application...

REM Clean old client files
if exist client rmdir /s /q client
if exist ClientApp.jar del ClientApp.jar
if exist ClientApp.exe del ClientApp.exe

REM Compile client files
echo Compiling client files...
javac -d . src\client\*.java
if %errorlevel% neq 0 (
    echo ❌ Client compilation failed!
    pause
    exit /b 1
)

REM Create client JAR
echo Creating ClientApp.jar...
jar cfm ClientApp.jar manifest.txt client\*.class
if %errorlevel% neq 0 (
    echo ❌ Client JAR creation failed!
    pause
    exit /b 1
)

REM Build EXE if Launch4j is available
if exist "C:\Program Files (x86)\Launch4j\launch4jc.exe" (
    echo Building ClientApp.exe...
    "C:\Program Files (x86)\Launch4j\launch4jc.exe" launch4j-config.xml
    if %errorlevel% eq 0 (
        echo ✅ ClientApp.exe created successfully!
    )
) else (
    echo Launch4j not found. Only JAR file created.
)

echo ✅ Client build completed!
pause
