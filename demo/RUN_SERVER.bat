@echo off
title Server - Quan ly phieu
echo ================================================================
echo           SERVER - HE THONG QUAN LY PHIEU
echo ================================================================
echo.
echo Dang khoi dong server...
echo.

if exist ServerApp.exe (
    echo Chay ServerApp.exe...
    ServerApp.exe
) else if exist ServerApp.jar (
    echo Chay ServerApp.jar...
    java -jar ServerApp.jar
) else (
    echo Khong tim thay file server!
    echo Vui long chay build-all.bat truoc.
    pause
    exit /b 1
)

echo.
echo Server da dung.
pause
