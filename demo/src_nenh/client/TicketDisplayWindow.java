package client;

import javax.swing.*;
import java.awt.*;

public class TicketDisplayWindow extends JFrame {
    private JPanel digitPanel;
    private JLabel departmentLabel;
    private String departmentName;
    
    public TicketDisplayWindow(String departmentName) {
        this.departmentName = departmentName;
        initializeComponents();
    }
    
    private void initializeComponents() {
        setTitle("ỦY BAN NHÂN DÂN PHƯỜNG NÊNH");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setUndecorated(true);
        
        // Hiển thị trên màn hình phụ thứ 2
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        if (screens.length > 1) {
            // Nếu có màn hình phụ, hiển thị trên màn hình thứ 2
            GraphicsDevice secondScreen = screens[1];
            Rectangle bounds = secondScreen.getDefaultConfiguration().getBounds();
            setBounds(bounds);
        }
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        // Header labels
        JLabel headerLabel = new JLabel("ỦY BAN NHÂN DÂN PHƯỜNG NẾNH");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subHeaderLabel = new JLabel("Kính chào quý khách");
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        subHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel codeLabel = new JLabel("Xin mời mã số");
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel cho các chữ số trong ô màu đỏ (có thể có nhiều số)
        digitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        digitPanel.setBackground(Color.WHITE);
        digitPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Department name
        departmentLabel = new JLabel(departmentName != null ? departmentName : "---");
        departmentLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        departmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components with spacing
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(subHeaderLabel);
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(codeLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(digitPanel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(departmentLabel);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel);
        
        // Khởi tạo với số 000
        updateTicketNumber("000");
    }
    
    public void updateTicketNumber(String ticketNumber) {
        System.out.println("Debug - updateTicketNumber called with: " + ticketNumber);
        digitPanel.removeAll();
        
        if (ticketNumber != null && !ticketNumber.isEmpty()) {
            try {
                // Tạo label cho từng chữ số
                for (int i = 0; i < ticketNumber.length(); i++) {
                    JLabel digitLabel = new JLabel(String.valueOf(ticketNumber.charAt(i)));
                    digitLabel.setFont(new Font("Arial", Font.BOLD, 120));
                    digitLabel.setForeground(Color.BLUE);
                    // digitLabel.setBackground(Color.RED);
                    digitLabel.setOpaque(true);
                    digitLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    digitLabel.setVerticalAlignment(SwingConstants.CENTER);
                    digitLabel.setPreferredSize(new Dimension(100, 150));
                    digitPanel.add(digitLabel);
                }
                
                System.out.println("Debug - Displayed number: " + ticketNumber);
            } catch (Exception e) {
                System.out.println("Debug - Error displaying number: " + e.getMessage());
                // Nếu có lỗi, hiển thị 000
                for (int i = 0; i < 3; i++) {
                    JLabel digitLabel = new JLabel("0");
                    digitLabel.setFont(new Font("Arial", Font.BOLD, 120));
                    digitLabel.setForeground(Color.BLUE);
                    digitLabel.setBackground(Color.RED);
                    digitLabel.setOpaque(true);
                    digitLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    digitLabel.setVerticalAlignment(SwingConstants.CENTER);
                    digitLabel.setPreferredSize(new Dimension(100, 150));
                    digitPanel.add(digitLabel);
                }
            }
        } else {
            System.out.println("Debug - Empty or null ticket number");
            // Hiển thị 000 khi không có số
            for (int i = 0; i < 3; i++) {
                JLabel digitLabel = new JLabel("0");
                digitLabel.setFont(new Font("Arial", Font.BOLD, 120));
                digitLabel.setForeground(Color.BLUE);
                digitLabel.setBackground(Color.RED);
                digitLabel.setOpaque(true);
                digitLabel.setHorizontalAlignment(SwingConstants.CENTER);
                digitLabel.setVerticalAlignment(SwingConstants.CENTER);
                digitLabel.setPreferredSize(new Dimension(100, 150));
                digitPanel.add(digitLabel);
            }
        }
        
        digitPanel.revalidate();
        digitPanel.repaint();
    }
    
    public void updateDepartment(String department) {
        if (department != null && !department.isEmpty()) {
            departmentLabel.setText(department);
            this.departmentName = department;
        } else {
            departmentLabel.setText("---");
        }
    }
    
    public void clearDisplay() {
        updateTicketNumber("000");
        departmentLabel.setText(departmentName != null ? departmentName : "---");
    }
}

