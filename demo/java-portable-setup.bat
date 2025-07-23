@echo off
title Java Portable Setup
echo ================================================================
echo            TAI JAVA PORTABLE (KHONG CAN CAI DAT)
echo ================================================================
echo.
echo Script nay se huong dan tai Java portable
echo.

echo 🔧 CAC BUOC THUC HIEN:
echo.
echo 1. Mo browser va truy cap:
echo    https://github.com/adoptium/temurin8-binaries/releases
echo.
echo 2. Tim file: OpenJDK8U-jre_x64_windows_hotspot_8uXXX.zip
echo.
echo 3. Tai ve va giai nen vao thu muc:
echo    d:\uyban\java-portable\
echo.
echo 4. Chay script: setup-portable-java.bat
echo.

pause

echo.
echo 🌐 Mo trang download...
start https://github.com/adoptium/temurin8-binaries/releases

echo.
echo Sau khi tai xong, chay: setup-portable-java.bat
pause
