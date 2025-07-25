package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ClientUI extends JFrame {
    private JLabel statusLabel;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JPanel mainPanel;
    private TicketDisplayWindow ticketDisplayWindow;
    private JCheckBox bottomCheckBox; // Add checkbox field

    // Lưu danh sách phiếu cho từng bộ phận
    private final Map<String, List<String[]>> ticketsMap = new ConcurrentHashMap<>();
    private String currentDepartment = null;
    private TicketPanel currentTicketPanel = null;

    public ClientUI() {
        setTitle("TRUNG TÂM PHỤC VỤ HÀNH CHÍNH CÔNG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setExtendedState(JFrame.MAXIMIZED_BOTH);
        // setUndecorated(true);
        setSize(900, 700); // hoặc kích thước bạn muốn
        setLocationRelativeTo(null);

        // Kiểm tra kết nối server trước khi khởi tạo giao diện
        if (!testServerConnection()) {
            showConnectionErrorAndExit();
            return;
        }

        initializeUI();
        connectToServer();
    }

    private boolean testServerConnection() {
        try {
            Socket testSocket = new Socket();
            testSocket.connect(new InetSocketAddress("localhost", 8888), 3000);
            // testSocket.connect(new InetSocketAddress("10.83.198.168", 8888), 3000);
            testSocket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void showConnectionErrorAndExit() {
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

    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Tiêu đề lớn giống server
        JLabel title = new JLabel("TRUNG TÂM PHỤC VỤ HÀNH CHÍNH CÔNG", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 39));
        title.setForeground(Color.RED);

        JLabel subtitle = new JLabel("Chọn để làm việc.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 29));
        subtitle.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        int n = Config.DEPARTMENTS.size();
        int row = (int) Math.ceil(n / 3.0);
        JPanel gridPanel = new JPanel(new GridLayout(row, 3, 16, 16));
        gridPanel.setBackground(Color.WHITE);

        // Khởi tạo map cho từng bộ phận
        for (String name : Config.DEPARTMENTS) {
            ticketsMap.put(name, new ArrayList<>());
        }

        for (String name : Config.DEPARTMENTS) {
            String counterNumber = Config.getDepartmentCounter(name);
            String buttonText = "<html><div style='text-align: center;'>" +
                    "<div style='font-size: 16px; color: #FFD700;'>Số Quầy: " + counterNumber + "</div>" +
                    "<div style='margin-top: 8px;'>" + name.replace("/", "<br>") + "</div>" +
                    "</div></html>";

            JButton btn = new JButton(buttonText);
            btn.setFont(new Font("Arial", Font.BOLD, 31));
            btn.setBackground(new Color(100, 140, 255));
            btn.setForeground(Color.WHITE);
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(80, 120, 235));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(100, 140, 255));
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(60, 100, 215));
                }
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    btn.setBackground(new Color(80, 120, 235));
                }
            });

            btn.addActionListener(e -> {
                currentDepartment = name;
                currentTicketPanel = new TicketPanel(currentDepartment, this);

                // Chỉ mở màn hình hiển thị nếu có 2 màn hình
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] screens = ge.getScreenDevices();
                if (screens.length > 1) {
                    showTicketDisplayWindow(currentDepartment);
                }

                if (out != null) {
                    out.println("GET_TICKETS|" + currentDepartment);
                }
                setContentPane(currentTicketPanel);
                revalidate();
                repaint();
            });

            gridPanel.add(btn);
        }
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 25));
        statusLabel.setForeground(Color.GRAY);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(gridPanel, BorderLayout.CENTER);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add checkbox at bottom-left
        bottomCheckBox = new JCheckBox("TV");
        bottomCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomCheckBox.setBackground(new Color(245, 245, 245));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(bottomCheckBox);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 8888);
                // socket = new Socket("10.83.198.168", 8888);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                updateStatus("Đã kết nối tới máy chủ!");
                
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Server: " + line);
                    // Nhận phiếu mới từ server
                    if (line.startsWith("NEW_TICKET|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 4) {
                            String dept = parts[1];
                            String code = parts[2];
                            String status = parts[3];
                            ticketsMap.get(dept).add(new String[]{code, status});
                            // Nếu đang ở đúng bộ phận thì cập nhật bảng
                            if (dept.equals(currentDepartment) && currentTicketPanel != null) {
                                SwingUtilities.invokeLater(() -> currentTicketPanel.addTicket(code, status));
                            }
                        }
                        
                    }
                    // Nhận toàn bộ phiếu từ server
                    if (line.startsWith("ALL_TICKETS|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 2) {
                            String dept = parts[1];
                            List<String[]> tickets = new ArrayList<>();
                            for (int i = 2; i + 1 < parts.length; i += 2) {
                                tickets.add(new String[]{parts[i], parts[i + 1]});
                            }
                            ticketsMap.put(dept, tickets);
                            // Nếu đang ở đúng bộ phận thì cập nhật bảng
                            if (dept.equals(currentDepartment) && currentTicketPanel != null) {
                                SwingUtilities.invokeLater(() -> {
                                    currentTicketPanel.clearTickets();
                                    for (String[] t : tickets) {
                                        currentTicketPanel.addTicket(t[0], t[1]);
                                    }
                                });
                            }
                        }
                    }
                    
                    // Nhận cập nhật trạng thái phiếu từ server
                    if (line.startsWith("UPDATE_TICKET|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 4) {
                            String dept = parts[1];
                            String code = parts[2];
                            String status = parts[3];
                            
                            // Cập nhật màn hình hiển thị khi có phiếu đang phục vụ
                            if (("Đang phục vụ".equals(status) || "Đang xử lý".equals(status)) && dept.equals(currentDepartment)) {
                                SwingUtilities.invokeLater(() -> updateTicketDisplay(code));
                            }
                            
                            // Cập nhật trong map với synchronized
                            synchronized (ticketsMap) {
                                List<String[]> tickets = ticketsMap.get(dept);
                                if (tickets != null) {
                                    for (String[] ticket : tickets) {
                                        if (ticket[0].equals(code)) {
                                            ticket[1] = status;
                                            break;
                                        }
                                    }
                                }
                            }
                            
                            // Nếu đang ở đúng bộ phận thì cập nhật bảng
                            if (dept.equals(currentDepartment) && currentTicketPanel != null) {
                                SwingUtilities.invokeLater(() -> {
                                    currentTicketPanel.clearTickets();
                                    List<String[]> tickets = ticketsMap.get(dept);
                                    if (tickets != null) {
                                        List<String[]> ticketsCopy = new ArrayList<>(tickets);
                                        for (String[] t : ticketsCopy) {
                                            currentTicketPanel.addTicket(t[0], t[1]);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    
                    // Nhận yêu cầu xóa phiếu từ server
                    if (line.startsWith("REMOVE_TICKET|")) {
                        String[] parts = line.split("\\|");
                        if (parts.length >= 3) {
                            String dept = parts[1];
                            String code = parts[2];
                            
                            // Xóa khỏi map
                            List<String[]> tickets = ticketsMap.get(dept);
                            if (tickets != null) {
                                tickets.removeIf(ticket -> ticket[0].equals(code));
                            }
                            
                            // Nếu đang ở đúng bộ phận thì cập nhật bảng
                            if (dept.equals(currentDepartment) && currentTicketPanel != null) {
                                SwingUtilities.invokeLater(() -> {
                                    currentTicketPanel.clearTickets();
                                    for (String[] t : tickets) {
                                        currentTicketPanel.addTicket(t[0], t[1]);
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Mất kết nối với máy chủ!");
                    showReconnectionDialog();
                });
            }
        }).start();
    }

    private void showReconnectionDialog() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Mất kết nối với máy chủ!\n\n" +
            "Bạn có muốn thử kết nối lại không?",
            "Mất kết nối",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            // Thử kết nối lại
            if (testServerConnection()) {
                connectToServer();
            } else {
                showConnectionErrorAndExit();
            }
        } else {
            System.exit(0);
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void showMainPanel() {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }

    public void sendCallNextRequest(String departmentName) {
        if (out != null) {
            out.println("CALL_NEXT|" + departmentName);
            updateStatus("Đã gửi yêu cầu gọi tiếp cho " + departmentName);
            
            // Debug: In ra danh sách phiếu hiện tại
            System.out.println("Debug - Department: " + departmentName);
            List<String[]> tickets = ticketsMap.get(departmentName);
            if (tickets != null) {
                // Tạo copy để tránh ConcurrentModificationException
                List<String[]> ticketsCopy = new ArrayList<>(tickets);
                System.out.println("Debug - Số phiếu trong map: " + ticketsCopy.size());
                for (String[] ticket : ticketsCopy) {
                    System.out.println("Debug - Phiếu: " + ticket[0] + " - Trạng thái: " + ticket[1]);
                }
                
                // Cập nhật màn hình hiển thị khi nhấn "Tiếp"
                if (ticketDisplayWindow != null && ticketDisplayWindow.isVisible()) {
                    if (!ticketsCopy.isEmpty()) {
                        String ticketToDisplay = null;
                        // Tìm phiếu đang phục vụ trước
                        for (String[] ticket : ticketsCopy) {
                            if ("Đang phục vụ".equals(ticket[1]) || "Đang xử lý".equals(ticket[1])) {
                                ticketToDisplay = ticket[0];
                                System.out.println("Debug - Tìm thấy phiếu đang phục vụ: " + ticketToDisplay);
                                break;
                            }
                        }
                        // Nếu không có phiếu đang phục vụ, tìm phiếu chờ đầu tiên
                        if (ticketToDisplay == null) {
                            for (String[] ticket : ticketsCopy) {
                                if ("Chờ".equals(ticket[1])) {
                                    ticketToDisplay = ticket[0];
                                    System.out.println("Debug - Tìm thấy phiếu chờ: " + ticketToDisplay);
                                    break;
                                }
                            }
                        }
                        
                        // Nếu vẫn không có, lấy phiếu đầu tiên
                        if (ticketToDisplay == null && !ticketsCopy.isEmpty()) {
                            ticketToDisplay = ticketsCopy.get(0)[0];
                            System.out.println("Debug - Lấy phiếu đầu tiên: " + ticketToDisplay);
                        }
                        
                        // Hiển thị số phiếu
                        if (ticketToDisplay != null) {
                            System.out.println("Debug - Hiển thị số: " + ticketToDisplay);
                            final String finalTicket = ticketToDisplay;
                            SwingUtilities.invokeLater(() -> ticketDisplayWindow.updateTicketNumber(finalTicket));
                        } else {
                            System.out.println("Debug - Không có phiếu nào để hiển thị");
                        }
                    } else {
                        System.out.println("Debug - Danh sách phiếu trống");
                    }
                } else {
                    System.out.println("Debug - Cửa sổ hiển thị không tồn tại hoặc không hiển thị");
                }
            } else {
                System.out.println("Debug - Tickets map null cho department: " + departmentName);
            }
        }
    }

    public void sendRepeatCallRequest(String departmentName) {
        if (out != null) {
            out.println("REPEAT_CALL|" + departmentName);
            updateStatus("Đã gửi yêu cầu nhắc lại cho " + departmentName);
        }
    }

    public void sendCompleteTicketRequest(String departmentName) {
        if (out != null) {
            out.println("COMPLETE_TICKET|" + departmentName);
            updateStatus("Đã gửi yêu cầu hoàn thành phiếu cho " + departmentName);
        }
    }

    public void showTicketDisplayWindow(String departmentName) {
        if (ticketDisplayWindow == null || !ticketDisplayWindow.isDisplayable()) {
            ticketDisplayWindow = new TicketDisplayWindow(departmentName);
        } else {
            ticketDisplayWindow.updateDepartment(departmentName);
        }
        ticketDisplayWindow.setVisible(true);
    }

    public void updateTicketDisplay(String ticketNumber) {
        if (ticketDisplayWindow != null && ticketDisplayWindow.isVisible()) {
            ticketDisplayWindow.updateTicketNumber(ticketNumber);
        }
    }

    public void clearTicketDisplay() {
        if (ticketDisplayWindow != null && ticketDisplayWindow.isVisible()) {
            ticketDisplayWindow.clearDisplay();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientUI().setVisible(true);
        });
    }
}

