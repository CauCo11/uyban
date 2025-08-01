package server;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerUI extends JFrame {
    private JLabel statusLabel;
    private ServerSocket serverSocket;

    // Lưu danh sách phiếu cho từng bộ phận (dùng Ticket thay vì String)
    private Map<String, List<Ticket>> ticketsMap = new HashMap<>();
    // Đếm số phiếu từng bộ phận
    private Map<String, Integer> ticketCounter = new HashMap<>();
    // Đếm số phiếu đã gọi cho từng bộ phận
    private Map<String, Integer> calledCounter = new HashMap<>();
    private final List<PrintWriter> clientOutputs = Collections.synchronizedList(new ArrayList<>());
    
    // Queue để xử lý audio requests tuần tự
    private final BlockingQueue<AudioRequest> audioQueue = new LinkedBlockingQueue<>();
    
    // Class để lưu thông tin audio request
    private static class AudioRequest {
        String ticketCode;
        String counterNumber;
        String department;
        
        AudioRequest(String ticketCode, String counterNumber, String department) {
            this.ticketCode = ticketCode;
            this.counterNumber = counterNumber;
            this.department = department;
        }
    }

    public ServerUI() {
        setTitle("Phần mềm máy chủ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full màn hình
        setUndecorated(true); // Ẩn thanh title
        setLocationRelativeTo(null);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Tiêu đề
        JLabel title = new JLabel("TRUNG TÂM PHỤC VỤ HÀNH CHÍNH CÔNG", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 39)); // Tăng từ 34 lên 39
        title.setForeground(Color.RED);

        // Phụ đề
        JLabel subtitle = new JLabel("Chọn để nhận phiếu.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 29)); // Tăng từ 24 lên 29
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
            // Lấy số quầy cho bộ phận
            String counterNumber = Config.getDepartmentCounter(name);
            
            // Tạo text với số quầy ở trên và tên bộ phận ở dưới
            String buttonText = "<html><div style='text-align: center;'>" + 
                               "<div style='font-size: 16px; color: #FFD700;'>Số Quầy: " + counterNumber + "</div>" +
                               "<div style='margin-top: 8px;'>" + name.replace("/", "<br>") + "</div>" +
                               "</div></html>";
            
            JButton btn = new JButton(buttonText);
            btn.setFont(new Font("Arial", Font.BOLD, 31)); // Tăng từ 26 lên 31
            btn.setBackground(new Color(100, 140, 255));
            btn.setForeground(Color.WHITE);
            btn.setVerticalAlignment(SwingConstants.CENTER);
            btn.setHorizontalAlignment(SwingConstants.CENTER);

            btn.addActionListener(e -> {
                // Tối màu nút và vô hiệu hóa ngay khi nhấn
                btn.setBackground(new Color(60, 80, 150));
                btn.setEnabled(false);
                
                // Thực hiện tất cả thao tác trong thread riêng
                new Thread(() -> {
                    try {
                        // Tăng số phiếu
                        int count = ticketCounter.get(name) + 1;
                        ticketCounter.put(name, count);
                        String counterNumberNew = Config.getDepartmentCounter(name);
                        String ticketCode = counterNumberNew + String.format("%03d", count);
                        // Khi tạo mới, trạng thái là "chờ xử lý"
                        Ticket ticket = new Ticket(ticketCode, "chờ xử lý");
                        ticketsMap.get(name).add(ticket);
                        
                        // In phiếu ngay khi tạo số mới
                        try {
                            String serviceName = mapDepartmentToService(name);
                            AudioCaller.printTicket(ticketCode, serviceName);
                        } catch (Exception ex) {
                            // Silent error handling
                        }
                        
                        // Gửi cho tất cả client
                        synchronized (clientOutputs) {
                            for (PrintWriter out : clientOutputs) {
                                out.println("NEW_TICKET|" + name + "|" + ticketCode + "|chờ xử lý");
                            }
                        }
                        
                    } finally {
                        // Sau khi hoàn thành tất cả, sáng lại nút
                        SwingUtilities.invokeLater(() -> {
                            btn.setBackground(new Color(100, 140, 255)); // Màu gốc
                            btn.setEnabled(true);
                        });
                    }
                }).start();
            });

            gridPanel.add(btn);
        }
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 25)); // Tăng từ 20 lên 25
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
            calledCounter.put(name, 0);
        }

        // Khởi tạo audio processor thread
        startAudioProcessor();
    }

    private void startAudioProcessor() {
        Thread audioProcessor = new Thread(() -> {
            while (true) {
                try {
                    AudioRequest request = audioQueue.take(); // Chờ cho đến khi có request
                    processAudioRequest(request);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        audioProcessor.setDaemon(true);
        audioProcessor.start();
    }
    
    private void processAudioRequest(AudioRequest request) {
        try {
            AudioCaller.playTicketCall(request.ticketCode, request.counterNumber);
        } catch (Exception e) {
            // Silent error handling
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(8888); // Chọn port phù hợp
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Xử lý mỗi client ở thread riêng
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            // Silent error handling
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

                // Xử lý yêu cầu lấy phiếu đang phục vụ cho queue display
                if (line.startsWith("GET_SERVING_TICKETS")) {
                    StringBuilder sb = new StringBuilder("SERVING_TICKETS");
                    for (Map.Entry<String, List<Ticket>> entry : ticketsMap.entrySet()) {
                        String dept = entry.getKey();
                        List<Ticket> tickets = entry.getValue();
                        String counterNumber = Config.getDepartmentCounter(dept);
                        
                        // Tìm phiếu đang phục vụ
                        String servingTicket = "----";
                        for (Ticket ticket : tickets) {
                            if ("đang xử lý".equals(ticket.getStatus())) {
                                servingTicket = ticket.getCode();
                                break;
                            }
                        }
                        
                        sb.append("|").append(dept).append("|").append(counterNumber).append("|").append(servingTicket);
                    }
                    out.println(sb.toString());
                    continue;
                }

                // Xử lý yêu cầu gọi tiếp
                if (line.startsWith("CALL_NEXT|")) {
                    String dept = line.substring("CALL_NEXT|".length());
                    List<Ticket> tickets = ticketsMap.get(dept);
                    int nextIndex = calledCounter.get(dept);
                    
                    if (tickets != null && nextIndex < tickets.size()) {
                        Ticket ticket = tickets.get(nextIndex);
                        
                        // Xóa tất cả phiếu có số nhỏ hơn phiếu hiện tại
                        String currentTicketNumber = ticket.getCode();
                        List<Ticket> ticketsToRemove = new ArrayList<>();
                        
                        for (Ticket t : tickets) {
                            if (compareTicketNumbers(t.getCode(), currentTicketNumber) < 0) {
                                ticketsToRemove.add(t);
                            }
                        }
                        
                        // Xóa các phiếu nhỏ hơn
                        for (Ticket t : ticketsToRemove) {
                            tickets.remove(t);
                            // Thông báo cho client xóa phiếu
                            synchronized (clientOutputs) {
                                for (PrintWriter clientOut : clientOutputs) {
                                    clientOut.println("REMOVE_TICKET|" + dept + "|" + t.getCode());
                                }
                            }
                        }
                        
                        // Cập nhật lại index sau khi xóa
                        nextIndex = Math.max(0, nextIndex - ticketsToRemove.size());
                        calledCounter.put(dept, nextIndex);
                        
                        // Tìm lại phiếu hiện tại sau khi xóa
                        if (nextIndex < tickets.size()) {
                            ticket = tickets.get(nextIndex);
                            
                            // Đổi trạng thái thành "đang xử lý"
                            ticket.setStatus("đang xử lý");
                            
                            // Tăng counter để lần sau gọi phiếu tiếp theo
                            calledCounter.put(dept, nextIndex + 1);
                            
                            // Lấy số quầy từ Config
                            String counterNumber = Config.getDepartmentCounter(dept);
                            
                            // Thêm vào queue thay vì gọi trực tiếp
                            audioQueue.offer(new AudioRequest(ticket.getCode(), counterNumber, dept));
                            
                            // Thông báo cho tất cả client về việc cập nhật trạng thái
                            synchronized (clientOutputs) {
                                for (PrintWriter clientOut : clientOutputs) {
                                    clientOut.println("UPDATE_TICKET|" + dept + "|" + ticket.getCode() + "|đang xử lý");
                                }
                            }
                        }
                    }
                    continue;
                }

                // Xử lý yêu cầu nhắc lại
                if (line.startsWith("REPEAT_CALL|")) {
                    String dept = line.substring("REPEAT_CALL|".length());
                    List<Ticket> tickets = ticketsMap.get(dept);
                    if (tickets != null && !tickets.isEmpty()) {
                        // Tìm phiếu đang xử lý để nhắc lại
                        for (Ticket ticket : tickets) {
                            if ("đang xử lý".equals(ticket.getStatus())) {
                                // Lấy số quầy từ Config
                                String counterNumber = Config.getDepartmentCounter(dept);
                                
                                // Thêm vào queue thay vì gọi trực tiếp
                                audioQueue.offer(new AudioRequest(ticket.getCode(), counterNumber, dept));
                                
                                break;
                            }
                        }
                    }
                    continue;
                }

                // Xử lý yêu cầu hoàn thành phiếu
                if (line.startsWith("COMPLETE_TICKET|")) {
                    String dept = line.substring("COMPLETE_TICKET|".length());
                    List<Ticket> tickets = ticketsMap.get(dept);
                    if (tickets != null && !tickets.isEmpty()) {
                        // Tìm phiếu đang xử lý để đánh dấu hoàn thành
                        for (Ticket ticket : tickets) {
                            if ("đang xử lý".equals(ticket.getStatus())) {
                                // Đổi trạng thái thành "hoàn thành"
                                ticket.setStatus("hoàn thành");
                                
                                // Thông báo cho tất cả client về việc cập nhật trạng thái
                                synchronized (clientOutputs) {
                                    for (PrintWriter clientOut : clientOutputs) {
                                        clientOut.println("UPDATE_TICKET|" + dept + "|" + ticket.getCode() + "|hoàn thành");
                                    }
                                }
                                break;
                            }
                        }
                    }
                    continue;
                }

                // Phản hồi lại client nếu muốn
                out.println("Server đã nhận: " + line);
            }
        } catch (IOException e) {
            // Silent error handling
        } finally {
            if (out != null) {
                clientOutputs.remove(out);
            }
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    // Phương thức so sánh số phiếu
    private int compareTicketNumbers(String ticket1, String ticket2) {
        try {
            // Lấy phần số từ mã phiếu (bỏ prefix)
            String num1 = ticket1.replaceAll("\\D", ""); // Loại bỏ ký tự không phải số
            String num2 = ticket2.replaceAll("\\D", "");
            
            int number1 = Integer.parseInt(num1);
            int number2 = Integer.parseInt(num2);
            
            return Integer.compare(number1, number2);
        } catch (NumberFormatException e) {
            // Nếu không parse được số, so sánh theo chuỗi
            return ticket1.compareTo(ticket2);
        }
    }

    // Ánh xạ tên bộ phận sang tên dịch vụ cho phiếu
    private String mapDepartmentToService(String departmentName) {
        switch (departmentName) {
            case "Tư pháp - Hộ tịch, Thanh tra":
                return "TƯ PHÁP - HỘ TỊCH, THANH TRA";
            case "Nội vụ - Y tế - Giáo dục và đào tạo":
                return "Nội vụ - Y tế - Giáo dục và đào tạo";
            case "Nội vụ, Y tế, Văn hóa - Xã hội, Khoa học - Công nghệ, Giáo dục - Đào tạo, Tôn giáo":
                return "NỘI VỤ, Y TẾ, VĂN HÓA - XÃ HỘI, KHOA HỌC - CÔNG NGHỆ, GIÁO DỤC - ĐÀO TẠO, TÔN GIÁO";
            case "Xây Dựng":
                return "XÂY DỰNG";
            case "Nông nghiệp - Môi Trường":
                return "NÔNG NGHIỆP - MÔI TRƯỜNG";
            case "Công thương, Tài chính":
                return "CÔNG THƯƠNG, TÀI CHÍNH";
            default:
                return departmentName.toUpperCase();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerUI serverUI = new ServerUI();
            serverUI.setVisible(true);
            new Thread(serverUI::startServer).start();
        });
    }
}