from PIL import Image, ImageDraw, ImageFont
import win32print
import win32ui
from PIL import ImageWin
import sys
from datetime import datetime

def create_ticket_image(queue_number: str, service: str = "TƯ PHÁP - HỘ TỊCH; NỘI VỤ", font_path="arial.ttf", font_size=24) -> Image.Image:
    # Tăng chiều cao để chứa đủ thời gian lấy phiếu
    width, height = 500, 700  # Tăng từ 600 lên 700
    img = Image.new("RGB", (width, height), "white")
    draw = ImageDraw.Draw(img)

    # Load font hỗ trợ tiếng Việt - to hơn
    try:
        font_normal = ImageFont.truetype(font_path, font_size + 4)      # Tăng thêm 4
        font_large = ImageFont.truetype(font_path, font_size + 24)      # Tăng từ +20 lên +24
        font_header = ImageFont.truetype(font_path, font_size + 8)      # Tăng từ +6 lên +8
    except:
        font_normal = ImageFont.load_default()
        font_large = ImageFont.load_default()
        font_header = ImageFont.load_default()

    # Hàm vẽ text nhiều dòng với xuống dòng tự động
    def draw_multiline_text(draw, text, position, font, fill="black", max_width=400, line_height=35):
        x, y = position
        words = text.split()
        lines = []
        current_line = []
        
        for word in words:
            test_line = ' '.join(current_line + [word])
            try:
                text_width = draw.textbbox((0, 0), test_line, font=font)[2]
            except:
                text_width = len(test_line) * font_size // 2
                
            if text_width <= max_width:
                current_line.append(word)
            else:
                if current_line:
                    lines.append(' '.join(current_line))
                    current_line = [word]
                else:
                    lines.append(word)
        
        if current_line:
            lines.append(' '.join(current_line))
        
        # Vẽ từng dòng và căn giữa
        for i, line in enumerate(lines):
            try:
                line_width = draw.textbbox((0, 0), line, font=font)[2]
            except:
                line_width = len(line) * font_size // 2
            
            line_x = (width - line_width) // 2
            line_y = y + i * line_height
            draw.text((line_x, line_y), line, fill=fill, font=font)
        
        return y + len(lines) * line_height

    # Vẽ border ngoài (khung chấm) - lớn hơn
    y_pos = 40  # Tăng margin top
    
    # Vẽ khung viền chấm - to hơn và rộng hơn
    border_width = 440  # Tăng từ 340 lên 440
    border_height = 620  # Tăng từ 580 lên 620 để chứa thêm thời gian lấy phiếu
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
    y_pos = 90  # Tăng margin top
    
    # Header - căn giữa chính xác
    header1 = "ỦY BAN NHÂN DÂN"
    header2 = "PHƯỜNG NẾNH"
    
    try:
        w1 = draw.textbbox((0, 0), header1, font=font_header)[2]
        w2 = draw.textbbox((0, 0), header2, font=font_header)[2]
    except:
        w1 = len(header1) * (font_size + 6) // 2
        w2 = len(header2) * (font_size + 6) // 2
    
    draw.text(((width - w1) // 2, y_pos), header1, fill="black", font=font_header)
    y_pos += 45  # Tăng spacing
    draw.text(((width - w2) // 2, y_pos), header2, fill="black", font=font_header)
    y_pos += 70  # Tăng spacing
    
    # Đường kẻ ngang dưới header - dày hơn
    draw.line([x_start + 40, y_pos, x_start + border_width - 40, y_pos], fill="black", width=2)
    y_pos += 80  # Tăng spacing
    
    # Số thứ tự lớn ở giữa - căn giữa chính xác
    try:
        number_w = draw.textbbox((0, 0), queue_number, font=font_large)[2]
    except:
        number_w = len(queue_number) * (font_size + 20) // 2
    
    draw.text(((width - number_w) // 2, y_pos), queue_number, fill="black", font=font_large)
    y_pos += 100  # Tăng spacing
    
    # Tên dịch vụ - cho phép xuống dòng tự động
    y_pos = draw_multiline_text(draw, service, (0, y_pos), font_normal, max_width=border_width - 80)
    y_pos += 25  # Extra spacing
    
    # Đường kẻ ngang - dày hơn
    draw.line([x_start + 40, y_pos, x_start + border_width - 40, y_pos], fill="black", width=2)
    y_pos += 45
    
    # Chỉ hiển thị thời gian lấy phiếu chi tiết (có giây)
    time_taken = datetime.now().strftime('%H:%M:%S - %d/%m/%Y')
    
    try:
        taken_w = draw.textbbox((0, 0), time_taken, font=font_normal)[2]
    except:
        taken_w = len(time_taken) * font_size // 2
    
    draw.text(((width - taken_w) // 2, y_pos), time_taken, fill="black", font=font_normal)

    return img

def print_image_to_printer(img: Image.Image, printer_name="80mm Series Printer"):
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
        return False

if __name__ == "__main__":
    if len(sys.argv) < 2:
        sys.exit(1)
    
    queue_number = sys.argv[1]
    service = " ".join(sys.argv[2:]) if len(sys.argv) > 2 else "TƯ PHÁP - HỘ TỊCH; NỘI VỤ"
    
    # Tạo ảnh phiếu
    image = create_ticket_image(queue_number, service)
    
    # # Lưu ảnh để debug
    # debug_filename = f"debug_ticket_{queue_number}_{datetime.now().strftime('%Y%m%d_%H%M%S')}.png"
    # try:
    #     image.save(debug_filename)
    #     print(f"💾 Đã lưu ảnh debug: {debug_filename}")
    # except Exception as e:
    #     print(f"⚠️ Không thể lưu ảnh debug: {e}")
    
    # In ra máy in trực tiếp
    if print_image_to_printer(image):
        print("✅ Đã in thành công!")
    else:
        print("❌ Lỗi khi in!")