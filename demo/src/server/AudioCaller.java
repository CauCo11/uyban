import java.io.IOException;

public class AudioCaller {
    
    // Đảm bảo chỉ có 1 audio process chạy tại 1 thời điểm
    private static final Object audioLock = new Object();
    
    public static void playTicketCall(String ticketNumber, String counterNumber) {
        synchronized (audioLock) {
            try {
                System.out.printf("[Java]: Bắt đầu phát âm thanh số %s quầy %s%n", ticketNumber, counterNumber);
                
                // Gọi file Python executable
                ProcessBuilder pb = new ProcessBuilder("d:\\uyban\\dist\\play_audio.exe", ticketNumber, counterNumber);
                pb.inheritIO(); // Để thấy output từ Python
                Process process = pb.start();
                
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    System.out.printf("✔ Java: Đã hoàn thành phát âm thanh số %s quầy %s%n", ticketNumber, counterNumber);
                } else {
                    System.out.printf("❌ Java: Lỗi khi phát âm thanh số %s quầy %s (Exit code: %d)%n", ticketNumber, counterNumber, exitCode);
                }

            } catch (IOException | InterruptedException e) {
                System.err.printf("❌ Java: Lỗi khi gọi Python executable cho số %s quầy %s:%n", ticketNumber, counterNumber);
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        String number = "1001";
        String counter = "9";
        playTicketCall(number, counter);
    }
}
