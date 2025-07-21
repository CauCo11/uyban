@echo off
echo Building Java Client Application...

REM Compile Java files
echo Compiling Java files...
javac -d . src\client\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create JAR file
echo Creating JAR file...
jar cfm ClientApp.jar manifest.txt client\*.class
if %errorlevel% neq 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

REM Test JAR file
echo Testing JAR file...
echo You can test with: java -jar ClientApp.jar

REM Build EXE (if Launch4j is installed)
if exist "C:\Program Files (x86)\Launch4j\launch4jc.exe" (
    echo Building EXE file...
    "C:\Program Files (x86)\Launch4j\launch4jc.exe" launch4j-config.xml
    if %errorlevel% eq 0 (
        echo Build successful! ClientApp.exe created.
    ) else (
        echo EXE build failed!
    )
) else (
    echo Launch4j not found. Please install Launch4j to create EXE file.
    echo JAR file ClientApp.jar is ready for use.
)

echo.
echo Build process completed!
pause
