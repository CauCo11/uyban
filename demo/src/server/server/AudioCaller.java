package server;

import java.io.IOException;
import java.io.File;

public class AudioCaller {
    
    // Đảm bảo chỉ có 1 audio process chạy tại 1 thời điểm
    private static final Object audioLock = new Object();
    
    public static void playTicketCall(String ticketNumber, String counterNumber) {
        synchronized (audioLock) {
            try {
                // Tự động tìm đường dẫn file play_audio.exe
                String audioExePath = getAudioExecutablePath();
                
                if (audioExePath == null) {
                    return;
                }
                
                // Gọi file Python executable
                ProcessBuilder pb = new ProcessBuilder(audioExePath, ticketNumber, counterNumber);
                Process process = pb.start();
                
                int exitCode = process.waitFor();

            } catch (IOException | InterruptedException e) {
                // Silent error handling
            }
        }
    }
    
    public static void printTicket(String ticketNumber, String service) {
        try {
            // Tự động tìm đường dẫn file in_phieu.exe
            String printExePath = getPrintExecutablePath();
            
            if (printExePath == null) {
                return;
            }
            
            // Gọi file Python executable để in phiếu
            ProcessBuilder pb = new ProcessBuilder(printExePath, ticketNumber, service);
            Process process = pb.start();
            
            int exitCode = process.waitFor();

        } catch (IOException | InterruptedException e) {
            // Silent error handling
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
                return file.getAbsolutePath();
            }
        }
        
        return null;
    }
    
    private static String getPrintExecutablePath() {
        // Thử các đường dẫn có thể có cho file in phiếu
        String[] possiblePaths = {
            "in_phieu.exe",
            "./in_phieu.exe",
            "d:\\uyban\\dist\\in_phieu.exe",
            "d:\\uyban\\demo\\src\\server\\in_phieu.exe",
            "../in_phieu.exe",
            "..\\dist\\in_phieu.exe"
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        playTicketCall("1001", "9");
        printTicket("1001", "TƯ PHÁP - HỘ TỊCH; NỘI VỤ");
    }
}
   
