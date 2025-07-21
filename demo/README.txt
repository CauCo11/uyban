===============================================================
           HE THONG QUAN LY PHIEU - HUONG DAN SU DUNG
===============================================================

📋 GIOI THIEU:
   Hệ thống quản lý phiếu cho Ủy ban nhân dân phường Nếnh
   Bao gồm: Server (máy chủ) và Client (máy khách)

⚙️ YÊU CẦU HỆ THỐNG:
   - Windows 7/8/10/11
   - Java Runtime Environment (xem tùy chọn bên dưới)
   - Kết nối mạng nội bộ (LAN)

☕ TÙY CHỌN JAVA (từ nhẹ đến nặng):

   🥇 KHUYEN CAO - OpenJDK JRE (~40MB):
   • Tai tu: https://adoptium.net/temurin/releases/
   • Chon: JRE, Windows x64, Latest LTS
   • Nhe nhat, mien phi, on dinh

   🥈 LUA CHON 2 - Java Portable (~35MB):
   • Khong can cai dat, chi giai nen
   • Chay: java-portable-setup.bat de tai
   • Sau do: setup-portable-java.bat de cau hinh

   🥉 LUA CHON 3 - Oracle JRE (~70MB):
   • Tai tu: https://java.com/download
   • Cai dat binh thuong
   • Nang hon nhung de dung

   📦 FULL - Oracle JDK (~150MB):
   • Chi can neu ban la developer
   • Khong can thiet cho user cuoi

🏗️ XÂY DỰNG ỨNG DỤNG:

   CÁCH 1 - Sử dụng Command Prompt (CMD):
   1. Mở Command Prompt (cmd) tại thư mục này
   2. Chạy: build-all.bat
   3. Đợi quá trình build hoàn thành

   CÁCH 2 - Sử dụng PowerShell:
   1. Mở PowerShell tại thư mục này
   2. Chạy: .\build-all.bat
   3. Đợi quá trình build hoàn thành

   CÁCH 3 - Nhấp đúp file:
   1. Nhấp đúp vào file build-all.bat
   2. Đợi quá trình build hoàn thành

🚀 CHẠY ỨNG DỤNG:

   💡 CHON 1 CACH PHIA DUOI:

   A) Voi Java da cai dat:
   1. RUN_SERVER.bat (hoac .\RUN_SERVER.bat trong PowerShell)
   2. RUN_CLIENT.bat (hoac .\RUN_CLIENT.bat trong PowerShell)

   B) Voi Java Portable:
   1. RUN_SERVER_PORTABLE.bat
   2. RUN_CLIENT_PORTABLE.bat

   C) Nhap dua file:
   1. Nhap dua ServerApp.exe (neu co)
   2. Nhap dua ClientApp.exe (neu co)

   CÁCH 4 - Chạy trực tiếp file exe:
   1. Nhấp đúp ServerApp.exe (nếu có)
   2. Nhấp đúp ClientApp.exe (nếu có)

   CÁCH 5 - Command line Java:
   1. java -jar ServerApp.jar
   2. java -jar ClientApp.jar

💡 LƯU Ý QUAN TRỌNG:
   - LUÔN CHẠY SERVER TRƯỚC, CLIENT SAU
   - Nếu dùng PowerShell, thêm ".\" trước tên file .bat
   - Nếu dùng Command Prompt (cmd), chạy trực tiếp tên file
   - Có thể nhấp đúp file .bat để chạy

🔧 KHẮC PHỤC LỖI POWERSH ELL:
   Nếu PowerShell báo lỗi "not recognized":
   - Thay vì: build-all.bat
   - Dùng: .\build-all.bat
   - Hoặc chuyển sang dùng Command Prompt (cmd)

📁 CẤU TRÚC FILE:
   src/client/         - Mã nguồn client
   src/server/         - Mã nguồn server
   ServerApp.jar       - Ứng dụng server
   ClientApp.jar       - Ứng dụng client
   ServerApp.exe       - File exe server (nếu có Launch4j)
   ClientApp.exe       - File exe client (nếu có Launch4j)
   build-all.bat       - Script build tất cả
   RUN_SERVER.bat      - Chạy server
   RUN_CLIENT.bat      - Chạy client

🔧 KHẮC PHỤC SỰ CỐ:
   - Lỗi "java not found": Cài Java hoặc dùng portable
   - Lỗi "Port already in use": Tắt server cũ
   - Không kết nối: Kiểm tra firewall
   - Java portable không chạy: Kiểm tra đường dẫn file

💾 DUNG LƯỢNG SO SÁNH:
   • Ứng dụng (.jar): ~100KB
   • OpenJDK JRE: ~40MB  
   • Java Portable: ~35MB (không cài đặt)
   • Oracle JRE: ~70MB
   • Oracle JDK: ~150MB

===============================================================
