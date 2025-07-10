from gtts import gTTS
import pygame
import io
import sys
import tempfile
import os
import time

def play_number_digits(number_str, counter_number):
    digits = " ".join(number_str)
    counter_number2 = " ".join(counter_number)
    text = f"Mời số {digits} đến quầy {counter_number2}"
    print(f"[Phát]: {text}")

    tts = gTTS(text=text, lang='vi')
    
    # Tạo file tạm thời nhưng không auto-delete
    tmp_file = tempfile.NamedTemporaryFile(suffix='.mp3', delete=False)
    tmp_file.close()  # Đóng file để pygame có thể truy cập
    
    try:
        # Lưu file audio
        tts.save(tmp_file.name)
        
        # Phát bằng pygame
        pygame.mixer.init()
        pygame.mixer.music.load(tmp_file.name)
        pygame.mixer.music.play()
        
        # Chờ phát xong
        while pygame.mixer.music.get_busy():
            pygame.time.wait(100)
        
        # Dừng và giải phóng tài nguyên pygame
        pygame.mixer.music.stop()
        pygame.mixer.quit()
        
        # Chờ một chút để đảm bảo file được giải phóng
        time.sleep(0.1)
            
    finally:
        # Thử xóa file nhiều lần nếu cần
        max_attempts = 5
        for attempt in range(max_attempts):
            try:
                if os.path.exists(tmp_file.name):
                    os.unlink(tmp_file.name)
                    break
            except PermissionError:
                if attempt < max_attempts - 1:
                    time.sleep(0.1)  # Chờ 100ms rồi thử lại
                else:
                    print(f"Cảnh báo: Không thể xóa file tạm {tmp_file.name}")

# Main để nhận tham số từ Java
if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Sử dụng: python play_audio.py <số> <quầy>")
        sys.exit(1)
    
    number = sys.argv[1]
    counter = sys.argv[2]
    play_number_digits(number, counter)