import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TicketPanel extends JPanel {
    private DefaultTableModel tableModel;
    private ClientUI parent;
    private String departmentName;

    public TicketPanel(String departmentName, ClientUI parent) {
        this.parent = parent;
        this.departmentName = departmentName;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Nút trở về
        JButton backButton = new JButton("Trở về");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> parent.showMainPanel());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(backButton, BorderLayout.EAST); // Đổi từ WEST sang EAST

        // Tiêu đề bộ phận
        JLabel deptLabel = new JLabel(departmentName, SwingConstants.LEFT);
        deptLabel.setFont(new Font("Arial", Font.BOLD, 22));
        deptLabel.setForeground(Color.BLUE);

        JLabel subLabel = new JLabel("Phiếu", SwingConstants.LEFT);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setForeground(Color.BLACK);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(deptLabel);
        titlePanel.add(subLabel);

        topPanel.add(titlePanel, BorderLayout.CENTER);

        // Bảng phiếu
        String[] columns = {"Mã số", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);

        // Panel màu xám chứa bảng, dùng BorderLayout để bảng chiếm hết
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(210, 210, 210));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        String[] btns = {"nhắc lại", "Tiếp"};
        for (String txt : btns) {
            JButton b = new JButton(txt);
            b.setFont(new Font("Arial", Font.PLAIN, 18));

            // Add action for "Tiếp" button
            if (txt.equals("Tiếp")) {
                b.addActionListener(e -> {
                    parent.sendCallNextRequest(departmentName);
                });
            }
            
            // Add action for "nhắc lại" button
            if (txt.equals("nhắc lại")) {
                b.addActionListener(e -> {
                    parent.sendRepeatCallRequest(departmentName);
                });
            }
            
            // Add action for "Quay lại" button
            if (txt.equals("Quay lại")) {
                b.addActionListener(e -> {
                    parent.sendCompleteTicketRequest(departmentName);
                });
            }

            buttonPanel.add(b);
        }
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        // Ghép các phần
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    }

    // Hàm để cập nhật dữ liệu từ server
    public void addTicket(String code, String status) {
        tableModel.addRow(new Object[]{code, status});
    }

    public void clearTickets() {
        tableModel.setRowCount(0);
    }
}