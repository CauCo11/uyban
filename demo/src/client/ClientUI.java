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

    // Lưu danh sách phiếu cho từng bộ phận
    private final Map<String, List<String[]>> ticketsMap = new ConcurrentHashMap<>();
    private String currentDepartment = null;
    private TicketPanel currentTicketPanel = null;

    public ClientUI() {
        setTitle("Phần mềm máy khách");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Kiểm tra kết nối server trước khi khởi tạo giao diện
        if (!testServerConnection()) {
            showConnectionErrorAndExit();
            return;
        }

        initializeUI();
        
        // Kết nối tới server khi khởi động
        connectToServer();
    }

    private boolean testServerConnection() {
        try {
            Socket testSocket = new Socket();
            testSocket.connect(new InetSocketAddress("trgc", 8888), 3000); // Timeout 3 giây
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

        // Status label giống server
        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        JLabel title = new JLabel("Ủy ban nhân dân phường Nếnh", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.RED);

        JLabel subtitle = new JLabel("Hãy chọn bộ phận làm việc.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        int n = Config.DEPARTMENTS.size();
        int cols = 3;
        int rows = (int) Math.ceil(n / (double) cols);
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // Khởi tạo map cho từng bộ phận
        for (String name : Config.DEPARTMENTS) {
            ticketsMap.put(name, new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            JButton btn = new JButton(Config.DEPARTMENTS.get(i));
            btn.setFont(new Font("Arial", Font.PLAIN, 22));
            btn.setBackground(new Color(100, 140, 255));
            btn.setForeground(Color.BLACK);

            btn.addActionListener(e -> {
                currentDepartment = btn.getText();
                currentTicketPanel = new TicketPanel(currentDepartment, this);
                // Gửi yêu cầu lấy danh sách phiếu cho bộ phận này
                if (out != null) {
                    out.println("GET_TICKETS|" + currentDepartment);
                }
                setContentPane(currentTicketPanel);
                revalidate();
                repaint();
            });

            int row = i / cols;
            int col = i % cols;

            if (row == rows - 1 && n % cols != 0) {
                int empty = cols - (n % cols);
                gbc.gridx = col + empty / 2;
            } else {
                gbc.gridx = col;
            }
            gbc.gridy = row;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gridPanel.add(btn, gbc);
        }
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(gridPanel, BorderLayout.CENTER);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 8888);
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
                            
                            // Cập nhật trong map
                            List<String[]> tickets = ticketsMap.get(dept);
                            if (tickets != null) {
                                for (String[] ticket : tickets) {
                                    if (ticket[0].equals(code)) {
                                        ticket[1] = status;
                                        break;
                                    }
                                }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientUI().setVisible(true);
        });
    }
}
