import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List; // Add this line

public class ServerUI extends JFrame {
    private JLabel statusLabel;
    private ServerSocket serverSocket;

    // Lưu danh sách phiếu cho từng bộ phận (dùng Ticket thay vì String)
    private Map<String, List<Ticket>> ticketsMap = new HashMap<>();
    // Đếm số phiếu từng bộ phận
    private Map<String, Integer> ticketCounter = new HashMap<>();
    private final List<PrintWriter> clientOutputs = Collections.synchronizedList(new ArrayList<>());

    public ServerUI() {
        setTitle("Phần mềm máy chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Tiêu đề
        JLabel title = new JLabel("Ủy ban nhân dân phường Nếnh", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.RED);

        // Phụ đề
        JLabel subtitle = new JLabel("Chọn để nhận phiếu.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitle.setForeground(Color.BLACK);

        // Panel chứa tiêu đề và phụ đề
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        // Lưới các nút bộ phận
        int n = Config.DEPARTMENTS.size();
        int row = (int) Math.ceil(n / 3.0);
        JPanel gridPanel = new JPanel(new GridLayout(row, 3, 16, 16));
        gridPanel.setBackground(Color.WHITE);
        for (String name : Config.DEPARTMENTS) {
            JButton btn = new JButton(name);
            btn.setFont(new Font("Arial", Font.PLAIN, 22));
            btn.setBackground(new Color(100, 140, 255));
            btn.setForeground(Color.BLACK);

            btn.addActionListener(e -> {
                // Tăng số phiếu
                int count = ticketCounter.get(name) + 1;
                ticketCounter.put(name, count);
                String ticketCode = String.format("%03d", count);
                // Khi tạo mới, trạng thái là "chờ xử lý"
                Ticket ticket = new Ticket(ticketCode, "chờ xử lý");
                ticketsMap.get(name).add(ticket);
                updateStatus("Đã thêm phiếu " + ticketCode + " vào " + name);
                // Gửi cho tất cả client
                synchronized (clientOutputs) {
                    for (PrintWriter out : clientOutputs) {
                        out.println("NEW_TICKET|" + name + "|" + ticketCode + "|chờ xử lý");
                    }
                }
            });

            gridPanel.add(btn);
        }
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        // Panel chứa tất cả
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(gridPanel, BorderLayout.CENTER);

        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Khởi tạo map cho từng bộ phận
        for (String name : Config.DEPARTMENTS) {
            ticketsMap.put(name, new ArrayList<>());
            ticketCounter.put(name, 0);
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(8888); // Chọn port phù hợp
            updateStatus("Máy chủ đang chạy trên cổng 8888...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                updateStatus("Có máy mới kết nối: " + clientSocket.getInetAddress());
                // Xử lý mỗi client ở thread riêng
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            updateStatus("Lỗi server: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        PrintWriter out = null;
        try (
            PrintWriter pw = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            out = pw;
            clientOutputs.add(out);
            out.println("Xin chào từ server!");
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Client gửi: " + line);

                // Xử lý yêu cầu lấy danh sách phiếu
                if (line.startsWith("GET_TICKETS|")) {
                    String dept = line.substring("GET_TICKETS|".length());
                    List<Ticket> tickets = ticketsMap.get(dept);
                    StringBuilder sb = new StringBuilder("ALL_TICKETS|" + dept);
                    if (tickets != null) {
                        for (Ticket t : tickets) {
                            sb.append("|").append(t.getCode()).append("|").append(t.getStatus());
                        }
                    }
                    out.println(sb.toString());
                    continue;
                }

                // Phản hồi lại client nếu muốn
                out.println("Server đã nhận: " + line);
            }
        } catch (IOException e) {
            System.out.println("Lỗi client: " + e.getMessage());
        } finally {
            if (out != null) {
                clientOutputs.remove(out);
            }
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerUI serverUI = new ServerUI();
            serverUI.setVisible(true);
            new Thread(serverUI::startServer).start();
            serverUI.updateStatus("Máy chủ đang chạy...");
        });
    }
}