@echo off
echo Building Client Application...

REM Compile all Java files
echo Compiling Java files...
cd src
javac -cp . client\*.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!

REM Create manifest
echo Creating manifest...
cd ..\
echo Main-Class: client.ClientUI > manifest.txt

REM Create JAR file
echo Creating JAR file...
jar cfm ClientApp.jar manifest.txt -C src .

if %ERRORLEVEL% NEQ 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

echo Build completed successfully!
echo JAR file: ClientApp.jar

REM Clean up
del manifest.txt

pause
