import java.io.IOException;
import java.io.File;

public class AudioCaller {
    
    // Đảm bảo chỉ có 1 audio process chạy tại 1 thời điểm
    private static final Object audioLock = new Object();
    
    public static void playTicketCall(String ticketNumber, String counterNumber) {
        synchronized (audioLock) {
            try {
                System.out.printf("[Java]: Bắt đầu phát âm thanh số %s quầy %s%n", ticketNumber, counterNumber);
                
                // Tự động tìm đường dẫn file play_audio.exe
                String audioExePath = getAudioExecutablePath();
                
                if (audioExePath == null) {
                    System.out.printf("⚠ Java: Không có file audio, chỉ hiển thị thông báo cho số %s quầy %s%n", ticketNumber, counterNumber);
                    return;
                }
                
                // Gọi file Python executable
                ProcessBuilder pb = new ProcessBuilder(audioExePath, ticketNumber, counterNumber);
                pb.inheritIO(); // Để thấy output từ Python
                Process process = pb.start();
                
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    System.out.printf("✔ Java: Đã hoàn thành phát âm thanh số %s quầy %s%n", ticketNumber, counterNumber);
                } else {
                    System.out.printf("❌ Java: Lỗi khi phát âm thanh số %s quầy %s (Exit code: %d)%n", ticketNumber, counterNumber, exitCode);
                }

            } catch (IOException | InterruptedException e) {
                System.err.printf("❌ Java: Lỗi khi gọi Python executable cho số %s quầy %s - %s%n", ticketNumber, counterNumber, e.getMessage());
                System.err.printf("⚠ Java: Hệ thống tiếp tục hoạt động mà không có âm thanh%n");
            }
        }
    }
    
    private static String getAudioExecutablePath() {
        // Thử các đường dẫn có thể có
        String[] possiblePaths = {
            "play_audio.exe",
            "./play_audio.exe",
            "d:\\uyban\\dist\\play_audio.exe",
            "d:\\uyban\\demo\\src\\server\\play_audio.exe",
            "../play_audio.exe",
            "..\\dist\\play_audio.exe"
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.canExecute()) {
                System.out.println("✓ Tìm thấy audio file: " + file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        }
        
        // Không tìm thấy file audio
        System.out.println("⚠ Không tìm thấy play_audio.exe trong các đường dẫn sau:");
        for (String path : possiblePaths) {
            System.out.println("  - " + new File(path).getAbsolutePath());
        }
        System.out.println("⚠ Hệ thống sẽ hoạt động mà không có âm thanh");
        return null;
    }
    
    public static void main(String[] args) {
        playTicketCall("1001", "9");
    }
}
