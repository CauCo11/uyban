@echo off
title Setup Portable Java
echo ================================================================
echo               SETUP JAVA PORTABLE
echo ================================================================
echo.

set JAVA_DIR=d:\uyban\java-portable
set JAVA_BIN=%JAVA_DIR%\bin

echo Tim Java portable trong: %JAVA_DIR%
echo.

if not exist "%JAVA_DIR%" (
    echo ❌ Khong tim thay thu muc java-portable
    echo    Vui long tao thu muc va giai nen Java vao day
    echo    Thu muc: %JAVA_DIR%
    pause
    exit /b 1
)

if not exist "%JAVA_BIN%\java.exe" (
    echo ❌ Khong tim thay java.exe
    echo    Kiem tra cau truc thu muc:
    echo    %JAVA_BIN%\java.exe
    pause
    exit /b 1
)

echo ✅ Tim thay Java portable!
echo.

REM Test Java
echo 🧪 Test Java...
"%JAVA_BIN%\java.exe" -version
if %errorlevel% neq 0 (
    echo ❌ Java khong hoat dong
    pause
    exit /b 1
)

echo.
echo ✅ Java portable hoat dong tot!
echo.

REM Tao script chay voi Java portable
echo 📝 Tao script chay voi Java portable...

echo @echo off > RUN_SERVER_PORTABLE.bat
echo title Server - Portable Java >> RUN_SERVER_PORTABLE.bat
echo echo Chay server voi Java portable... >> RUN_SERVER_PORTABLE.bat
echo "%JAVA_BIN%\java.exe" -jar ServerApp.jar >> RUN_SERVER_PORTABLE.bat
echo pause >> RUN_SERVER_PORTABLE.bat

echo @echo off > RUN_CLIENT_PORTABLE.bat
echo title Client - Portable Java >> RUN_CLIENT_PORTABLE.bat
echo echo Chay client voi Java portable... >> RUN_CLIENT_PORTABLE.bat
echo "%JAVA_BIN%\java.exe" -jar ClientApp.jar >> RUN_CLIENT_PORTABLE.bat
echo pause >> RUN_CLIENT_PORTABLE.bat

echo.
echo ✅ Setup hoan thanh!
echo.
echo 🚀 De chay voi Java portable:
echo    • Server: RUN_SERVER_PORTABLE.bat
echo    • Client: RUN_CLIENT_PORTABLE.bat
echo.
pause
