package tv;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class TVApp extends JFrame {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private QueueDisplayWindow queueDisplayWindow;

    public TVApp() {
        setTitle("TV Display - Queue Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Create and show queue display immediately on screen 2
        queueDisplayWindow = new QueueDisplayWindow();
        
        // Ensure it's positioned on screen 2 if available
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        if (screens.length > 1) {
            // Move to screen 2 and maximize
            GraphicsDevice secondScreen = screens[1];
            Rectangle bounds = secondScreen.getDefaultConfiguration().getBounds();
            queueDisplayWindow.setBounds(bounds);
            queueDisplayWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        queueDisplayWindow.setVisible(true);
        
        // Connect to server
        connectToServer();
        
        // Hide the main window (we only need the queue display)
        setVisible(false);
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("10.83.198.168", 8888);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                // Request serving tickets immediately
                out.println("GET_SERVING_TICKETS");
                
                String line;
                while ((line = in.readLine()) != null) {
                    // Handle serving tickets updates
                    if (line.startsWith("SERVING_TICKETS")) {
                        String[] parts = line.split("\\|");
                        SwingUtilities.invokeLater(() -> {
                            queueDisplayWindow.updateServingTicketsFromServer(parts);
                        });
                    }
                    
                    // Handle ticket updates
                    if (line.startsWith("UPDATE_TICKET|")) {
                        // Request fresh data when any ticket is updated
                        out.println("GET_SERVING_TICKETS");
                    }
                    
                    // Handle new tickets
                    if (line.startsWith("NEW_TICKET|")) {
                        // Request fresh data when new ticket is created
                        out.println("GET_SERVING_TICKETS");
                    }
                    
                    // Handle removed tickets
                    if (line.startsWith("REMOVE_TICKET|")) {
                        // Request fresh data when ticket is removed
                        out.println("GET_SERVING_TICKETS");
                    }
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    showConnectionError();
                });
            }
        }).start();
    }

    private void showConnectionError() {
        JOptionPane.showMessageDialog(
            null,
            "Không thể kết nối tới máy chủ!\n\n" +
            "Vui lòng kiểm tra:\n" +
            "• Máy chủ đã được khởi động\n" +
            "• Kết nối mạng\n" +
            "Ứng dụng sẽ thoát.",
            "Lỗi kết nối máy chủ",
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TVApp();
        });
    }
}
