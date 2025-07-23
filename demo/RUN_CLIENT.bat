@echo off
title Client - Quan ly phieu
echo ================================================================
echo           CLIENT - HE THONG QUAN LY PHIEU  
echo ================================================================
echo.
echo 💡 CAC TUY CHON JAVA (tu nhe den nang):
echo    1. OpenJDK JRE (~40MB) - Khuyen cao
echo    2. Oracle JRE (~70MB) 
echo    3. JDK day du (~150MB)
echo.
echo 🔍 Kiem tra Java...

REM Kiem tra Java
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ KHONG TIM THAY JAVA!
    echo.
    echo 📥 TAI JAVA NHE:
    echo    • OpenJDK JRE: https://adoptium.net/temurin/releases/
    echo    • Oracle JRE: https://java.com/download
    echo    • Portable Java: https://portapps.io/app/openjdk-jre-portable/
    echo.
    pause
    exit /b 1
) else (
    echo ✅ Java da san sang!
)

echo.
echo Dang khoi dong client...
echo.

if exist ClientApp.exe (
    echo Chay ClientApp.exe...
    ClientApp.exe
) else if exist ClientApp.jar (
    echo Chay ClientApp.jar...
    java -jar ClientApp.jar
) else (
    echo Khong tim thay file client!
    echo Vui long chay build-all.bat truoc.
    pause
    exit /b 1
)

echo.
echo Client da dong.
pause
