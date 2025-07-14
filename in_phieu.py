from PIL import Image, ImageDraw, ImageFont
import win32print
import win32ui
from PIL import ImageWin
import sys
from datetime import datetime

def create_ticket_image(queue_number: str, service: str = "TƯ PHÁP - HỘ TỊCH; NỘI VỤ", font_path="arial.ttf", font_size=24) -> Image.Image:
    # Tạo ảnh trắng với kích thước giống phiếu thật
    width, height = 384, 500  # Kích thước phù hợp
    img = Image.new("RGB", (width, height), "white")
    draw = ImageDraw.Draw(img)

    # Load font hỗ trợ tiếng Việt - to hơn
    try:
        font_normal = ImageFont.truetype(font_path, font_size)
        font_large = ImageFont.truetype(font_path, font_size + 20)  # Tăng từ +12 lên +20
        font_header = ImageFont.truetype(font_path, font_size + 6)   # Tăng từ +2 lên +6
    except:
        font_normal = ImageFont.load_default()
        font_large = ImageFont.load_default()
        font_header = ImageFont.load_default()

    # Vẽ border ngoài (khung chấm)
    y_pos = 30
    
    # Vẽ khung viền chấm - to hơn
    border_width = 340  # Tăng từ 300
    border_height = 420
    x_start = (width - border_width) // 2
    
    # Vẽ border trên và dưới - chấm to hơn
    for x in range(x_start, x_start + border_width, 6):  # Giảm khoảng cách từ 8 xuống 6
        draw.ellipse([x, y_pos, x+3, y_pos+3], fill="black")  # Tăng size chấm
        draw.ellipse([x, y_pos + border_height, x+3, y_pos + border_height+3], fill="black")
    
    # Vẽ border trái và phải - chấm to hơn
    for y in range(y_pos, y_pos + border_height, 6):
        draw.ellipse([x_start, y, x_start+3, y+3], fill="black")
        draw.ellipse([x_start + border_width, y, x_start + border_width+3, y+3], fill="black")
    
    # Nội dung bên trong khung
    y_pos = 70  # Tăng margin top
    
    # Header - căn giữa chính xác
    header1 = "ỦY BAN NHÂN DÂN"
    header2 = "PHƯỜNG NENH"
    
    try:
        w1 = draw.textbbox((0, 0), header1, font=font_header)[2]
        w2 = draw.textbbox((0, 0), header2, font=font_header)[2]
    except:
        w1 = len(header1) * (font_size + 6) // 2
        w2 = len(header2) * (font_size + 6) // 2
    
    draw.text(((width - w1) // 2, y_pos), header1, fill="black", font=font_header)
    y_pos += 40  # Tăng spacing
    draw.text(((width - w2) // 2, y_pos), header2, fill="black", font=font_header)
    y_pos += 60  # Tăng spacing
    
    # Đường kẻ ngang dưới header - dày hơn
    draw.line([x_start + 30, y_pos, x_start + border_width - 30, y_pos], fill="black", width=2)
    y_pos += 70  # Tăng spacing
    
    # Số thứ tự lớn ở giữa - căn giữa chính xác
    try:
        number_w = draw.textbbox((0, 0), queue_number, font=font_large)[2]
    except:
        number_w = len(queue_number) * (font_size + 20) // 2
    
    draw.text(((width - number_w) // 2, y_pos), queue_number, fill="black", font=font_large)
    y_pos += 90  # Tăng spacing
    
    # Tên dịch vụ - căn giữa trên 1 dòng duy nhất
    try:
        service_w = draw.textbbox((0, 0), service, font=font_normal)[2]
    except:
        service_w = len(service) * font_size // 2
    
    draw.text(((width - service_w) // 2, y_pos), service, fill="black", font=font_normal)
    y_pos += 35  # Single line spacing
    
    y_pos += 20  # Extra spacing
    
    # Đường kẻ ngang - dày hơn
    draw.line([x_start + 30, y_pos, x_start + border_width - 30, y_pos], fill="black", width=2)
    y_pos += 40
    
    # Thời gian - căn giữa
    time_str = datetime.now().strftime('%H:%M Th %w, %d thg %m, %Y')
    try:
        time_w = draw.textbbox((0, 0), time_str, font=font_normal)[2]
    except:
        time_w = len(time_str) * font_size // 2
    
    draw.text(((width - time_w) // 2, y_pos), time_str, fill="black", font=font_normal)

    return img

def print_image_to_printer(img: Image.Image, printer_name="POS-80C"):
    try:
        hPrinter = win32print.OpenPrinter(printer_name)
        hDC = win32ui.CreateDC()
        hDC.CreatePrinterDC(printer_name)

        hDC.StartDoc("UBND Queue Ticket")
        hDC.StartPage()

        # Chuyển đổi ảnh và in
        dib = ImageWin.Dib(img.convert("RGB"))
        dib.draw(hDC.GetHandleOutput(), (0, 0, img.width, img.height))

        hDC.EndPage()
        hDC.EndDoc()
        hDC.DeleteDC()
        win32print.ClosePrinter(hPrinter)
        
        return True
    except Exception as e:
        print(f"Lỗi khi in: {e}")
        return False

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Sử dụng: python in_phieu.py <số_thứ_tự> [dịch_vụ]")
        print("Ví dụ: python in_phieu.py 2006 \"TƯ PHÁP - HỘ TỊCH; NỘI VỤ\"")
        sys.exit(1)
    
    queue_number = sys.argv[1]
    service = " ".join(sys.argv[2:]) if len(sys.argv) > 2 else "TƯ PHÁP - HỘ TỊCH; NỘI VỤ"
    
    print("=== PREVIEW PHIẾU ===")
    print("ỦY BAN NHÂN DÂN")
    print("PHƯỜNG NENH")
    print(f"Số: {queue_number}")
    print(f"Dịch vụ: {service}")
    print("=== KẾT THÚC PREVIEW ===")
    
    # Tạo ảnh phiếu
    image = create_ticket_image(queue_number, service)
    
    # Lưu preview (optional)
    try:
        image.save("d:/uyban/preview_ticket.png")
        print("✅ Đã lưu preview: d:/uyban/preview_ticket.png")
    except:
        pass
    
    # In ra máy in
    if print_image_to_printer(image):
        print("✅ Đã in thành công!")
    else:
        print("❌ Lỗi khi in!")
        pass