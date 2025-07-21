@echo off
echo ================================
echo   BUILDING WITH AUTO UPDATE
echo ================================

REM Clean old builds
if exist "build\" rmdir /s /q "build"
mkdir build

REM Compile
echo [1/4] Compiling Java files...
cd src
javac -d ../build -cp . client/client/*.java

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

REM Create manifest
echo [2/4] Creating manifest...
cd ..
echo Main-Class: client.ClientUI > build/manifest.txt

REM Copy resources
echo [3/4] Copying resources...
copy app.version build/
copy version.json build/

REM Create JAR
echo [4/4] Creating JAR...
cd build
jar cfm ClientApp.jar manifest.txt client/*.class app.version

if %ERRORLEVEL% NEQ 0 (
    echo ❌ JAR creation failed!
    pause
    exit /b 1
)

move ClientApp.jar ../
cd ..

echo ✅ Build successful!
echo 📦 Output: ClientApp.jar
pause
