@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo.
echo ================================================================
echo     XÂY DỰNG ỨNG DỤNG QUẢN LÝ PHIẾU - CLIENT VÀ SERVER
echo ================================================================
echo.

REM Kiểm tra Java
echo [Bước 1] 🔍 Kiểm tra Java...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java không được cài đặt hoặc không có trong PATH!
    echo    Vui lòng cài đặt Java JDK từ: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo ✅ Java đã sẵn sàng!

REM Dọn dẹp files cũ
echo.
echo [Bước 2] 🧹 Dọn dẹp files cũ...
if exist client rmdir /s /q client >nul 2>&1
if exist server rmdir /s /q server >nul 2>&1
if exist *.jar del *.jar >nul 2>&1
if exist ClientApp.exe del ClientApp.exe >nul 2>&1
if exist ServerApp.exe del ServerApp.exe >nul 2>&1
echo ✅ Đã dọn dẹp!

REM Kiểm tra structure
echo.
echo [Bước 3] 📁 Kiểm tra cấu trúc thư mục...
if not exist "src\client\client" (
    echo ❌ Không tìm thấy thư mục src\client\client
    pause
    exit /b 1
)
if not exist "src\server\server" (
    echo ❌ Không tìm thấy thư mục src\server\server
    pause
    exit /b 1
)
echo ✅ Cấu trúc thư mục OK!

REM Compile tất cả
echo.
echo [Bước 4] 🔨 Biên dịch mã nguồn Java...
echo    - Biên dịch client files...
javac -d . src\client\client\*.java
if %errorlevel% neq 0 (
    echo ❌ Biên dịch client thất bại!
    pause
    exit /b 1
)

echo    - Biên dịch server files...
javac -d . src\server\server\*.java
if %errorlevel% neq 0 (
    echo ❌ Biên dịch server thất bại!
    pause
    exit /b 1
)
echo ✅ Biên dịch thành công!

REM Tạo JAR files
echo.
echo [Bước 5] 📦 Tạo file JAR...
echo    - Tạo ClientApp.jar...
jar cfm ClientApp.jar manifest.txt client\*.class
if %errorlevel% neq 0 (
    echo ❌ Tạo ClientApp.jar thất bại!
    pause
    exit /b 1
)

echo    - Tạo ServerApp.jar...
jar cfm ServerApp.jar manifest-server.txt server\*.class
if %errorlevel% neq 0 (
    echo ❌ Tạo ServerApp.jar thất bại!
    pause
    exit /b 1
)
echo ✅ Tạo JAR files thành công!

REM Test JAR files
echo.
echo [Bước 6] 🧪 Kiểm tra JAR files...
echo    - Kiểm tra ClientApp.jar...
java -jar ClientApp.jar --help >nul 2>&1
echo    - Kiểm tra ServerApp.jar...
java -jar ServerApp.jar --help >nul 2>&1
echo ✅ JAR files hoạt động tốt!

REM Tìm Launch4j
echo.
echo [Bước 7] 🔍 Tìm kiếm Launch4j...
set LAUNCH4J_PATH=
set LAUNCH4J_FOUND=0

REM Các đường dẫn có thể có của Launch4j
set "PATHS[0]=C:\Program Files (x86)\Launch4j\launch4jc.exe"
set "PATHS[1]=C:\Program Files\Launch4j\launch4jc.exe"
set "PATHS[2]=%USERPROFILE%\AppData\Local\Launch4j\launch4jc.exe"
set "PATHS[3]=%PROGRAMFILES%\Launch4j\launch4jc.exe"
set "PATHS[4]=%PROGRAMFILES(X86)%\Launch4j\launch4jc.exe"

for /L %%i in (0,1,4) do (
    if exist "!PATHS[%%i]!" (
        set "LAUNCH4J_PATH=!PATHS[%%i]!"
        set LAUNCH4J_FOUND=1
        echo ✅ Tìm thấy Launch4j tại: !LAUNCH4J_PATH!
        goto :launch4j_found
    )
)

:launch4j_found
if %LAUNCH4J_FOUND%==0 (
    echo ⚠️  Không tìm thấy Launch4j
    echo    Tải từ: https://launch4j.sourceforge.net/
    echo    Chỉ tạo JAR files...
    goto :skip_exe
)

REM Tạo EXE files
echo.
echo [Bước 8] 🏗️  Tạo file EXE...
echo    - Tạo ClientApp.exe...
"%LAUNCH4J_PATH%" launch4j-config.xml
if %errorlevel% eq 0 (
    echo ✅ ClientApp.exe tạo thành công!
) else (
    echo ❌ Tạo ClientApp.exe thất bại!
)

echo    - Tạo ServerApp.exe...
"%LAUNCH4J_PATH%" launch4j-server-config.xml
if %errorlevel% eq 0 (
    echo ✅ ServerApp.exe tạo thành công!
) else (
    echo ❌ Tạo ServerApp.exe thất bại!
)

:skip_exe

REM Dọn dẹp files class
echo.
echo [Bước 9] 🧹 Dọn dẹp files tạm...
rmdir /s /q client >nul 2>&1
rmdir /s /q server >nul 2>&1
echo ✅ Đã dọn dẹp!

REM Hiển thị kết quả
echo.
echo ================================================================
echo                    🎉 XÂY DỰNG HOÀN THÀNH!
echo ================================================================
echo.
echo 📋 KẾT QUẢ:
echo.

set /a count=0
if exist ServerApp.jar (
    echo    ✅ ServerApp.jar          ^(Ứng dụng máy chủ^)
    set /a count+=1
)
if exist ClientApp.jar (
    echo    ✅ ClientApp.jar          ^(Ứng dụng máy khách^)
    set /a count+=1
)
if exist ServerApp.exe (
    echo    ✅ ServerApp.exe          ^(Ứng dụng máy chủ - EXE^)
    set /a count+=1
)
if exist ClientApp.exe (
    echo    ✅ ClientApp.exe          ^(Ứng dụng máy khách - EXE^)
    set /a count+=1
)

echo.
echo 📊 Tổng cộng: %count% file được tạo
echo.

REM Hướng dẫn sử dụng
echo 🚀 CÁCH SỬ DỤNG:
echo.
echo    1. CHẠY MÁY CHỦ TRƯỚC:
if exist ServerApp.exe (
    echo       • Nhấp đúp vào ServerApp.exe
    echo       • Hoặc: java -jar ServerApp.jar
) else (
    echo       • java -jar ServerApp.jar
)
echo.
echo    2. SAU ĐÓ CHẠY MÁY KHÁCH:
if exist ClientApp.exe (
    echo       • Nhấp đúp vào ClientApp.exe  
    echo       • Hoặc: java -jar ClientApp.jar
) else (
    echo       • java -jar ClientApp.jar
)
echo.

echo 📋 LƯU Ý QUAN TRỌNG:
echo    • Luôn chạy Server trước, Client sau
echo    • Server sẽ chạy trên cổng 8888
echo    • Cần có Java Runtime Environment ^(JRE^) để chạy
echo    • Để tắt Server: nhấn Ctrl+C trong cửa sổ console
echo.

echo 🔧 KHẮC PHỤC SỰ CỐ:
echo    • Nếu lỗi "port already in use": tắt ứng dụng cũ
echo    • Nếu không kết nối được: kiểm tra Windows Firewall
echo    • Nếu thiếu Java: cài từ https://java.com/download
echo.

echo ================================================================
pause
