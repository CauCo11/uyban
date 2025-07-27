package tv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class QueueDisplayWindow extends JFrame {
    private DefaultTableModel tableModel;
    private JTable queueTable;
    
    public QueueDisplayWindow() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        setTitle("TV Display - Hàng đợi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        
        // Display on screen 2 by default
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        if (screens.length > 1) {
            // If there's a second screen, display on screen 2
            GraphicsDevice secondScreen = screens[1];
            Rectangle bounds = secondScreen.getDefaultConfiguration().getBounds();
            setBounds(bounds);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            // If only one screen, maximize on primary screen
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(70, 130, 180),
                    0, getHeight(), new Color(100, 150, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        // Table setup
        String[] columns = {"SỐ THỨ TỰ", "QUẦY"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        queueTable = new JTable(tableModel);
        queueTable.setFont(new Font("Arial", Font.BOLD, 36));
        queueTable.setRowHeight(80);
        queueTable.setBackground(Color.WHITE);
        queueTable.setForeground(Color.BLACK);
        queueTable.setSelectionBackground(new Color(70, 130, 180));
        queueTable.setSelectionForeground(Color.WHITE);
        queueTable.setGridColor(new Color(70, 130, 180));
        
        // Style the table header
        queueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 32));
        queueTable.getTableHeader().setBackground(new Color(50, 100, 150));
        queueTable.getTableHeader().setForeground(Color.WHITE);
        queueTable.getTableHeader().setPreferredSize(new Dimension(0, 60));
        queueTable.getTableHeader().setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Custom header renderer
        queueTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 32));
                setBackground(new Color(50, 100, 150));
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                return c;
            }
        });
        
        // Custom cell renderer
        queueTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(220, 230, 240));
                    }
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                
                int windowHeight = QueueDisplayWindow.this.getHeight();
                int fontSize = Math.max(16, Math.min(32, windowHeight / 20));
                setFont(new Font("Arial", Font.BOLD, fontSize));
                
                return c;
            }
        });
        
        // Column widths
        queueTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        queueTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(queueTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLoweredBevelBorder()
        ));
        scrollPane.getViewport().setBackground(new Color(70, 130, 180));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
        
        // Add component listener for responsive behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveLayout();
            }
        });
        
        // Initialize with empty data
        updateDisplay();
    }
    
    private void updateResponsiveLayout() {
        SwingUtilities.invokeLater(() -> {
            int windowHeight = getHeight();
            int windowWidth = getWidth();
            
            int numRows = tableModel.getRowCount();
            int availableHeight = windowHeight - 100;
            
            int headerHeight = Math.max(60, Math.min(100, windowHeight / 8));
            int headerFontSize = Math.max(24, Math.min(48, windowWidth / 20));
            
            queueTable.getTableHeader().setPreferredSize(new Dimension(0, headerHeight));
            queueTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, headerFontSize));
            
            int optimalRowHeight = numRows > 0 ? (availableHeight - headerHeight) / numRows : 60;
            int rowHeight = Math.max(50, Math.min(120, optimalRowHeight));
            queueTable.setRowHeight(rowHeight);
            
            int col1Width = (int)(windowWidth * 0.55);
            int col2Width = (int)(windowWidth * 0.45);
            
            queueTable.getColumnModel().getColumn(0).setPreferredWidth(col1Width);
            queueTable.getColumnModel().getColumn(1).setPreferredWidth(col2Width);
            
            queueTable.repaint();
        });
    }
    
    public void updateServingTicketsFromServer(String[] serverData) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            
            for (int i = 1; i + 2 < serverData.length; i += 3) {
                String department = serverData[i];
                String counterNumber = serverData[i + 1];
                String servingTicket = serverData[i + 2];
                
                rows.add(new Object[]{servingTicket, "QUẦY SỐ " + counterNumber, Integer.parseInt(counterNumber)});
            }
            
            // Sort by counter number
            rows.sort((o1, o2) -> Integer.compare((Integer)o1[2], (Integer)o2[2]));
            
            // Add sorted rows to table
            for (Object[] row : rows) {
                tableModel.addRow(new Object[]{row[0], row[1]});
            }
            
            queueTable.revalidate();
            queueTable.repaint();
        });
    }
    
    private void updateDisplay() {
        java.util.List<Object[]> defaultRows = new java.util.ArrayList<>();
        
        for (String dept : Config.DEPARTMENTS) {
            String counterNumber = Config.getDepartmentCounter(dept);
            defaultRows.add(new Object[]{"----", "QUẦY SỐ " + counterNumber, Integer.parseInt(counterNumber)});
        }
        
        defaultRows.sort((o1, o2) -> Integer.compare((Integer)o1[2], (Integer)o2[2]));
        
        for (Object[] row : defaultRows) {
            tableModel.addRow(new Object[]{row[0], row[1]});
        }
    }
}
