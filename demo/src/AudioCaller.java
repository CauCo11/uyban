import java.io.IOException;

public class AudioCaller {
    
    public static void playTicketCall(String ticketNumber, String counterNumber) {
        try {
            System.out.printf("[Java]: Gửi yêu cầu phát số %s quầy %s%n", ticketNumber, counterNumber);
            
            // Gọi file Python executable
            ProcessBuilder pb = new ProcessBuilder("d:\\uyban\\dist\\play_audio.exe", ticketNumber, counterNumber);
            pb.inheritIO(); // Để thấy output từ Python
            Process process = pb.start();
            
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("✔ Java: Đã hoàn thành phát âm thanh.");
            } else {
                System.out.println("❌ Java: Lỗi khi phát âm thanh (Exit code: " + exitCode + ")");
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Java: Lỗi khi gọi Python executable:");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String number = "1001";
        String counter = "9";
        playTicketCall(number, counter);
    }
}
