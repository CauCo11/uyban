package client;

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
        topPanel.add(backButton, BorderLayout.EAST);

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

        // Panel màu xám chứa bảng
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(210, 210, 210));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 16, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        String[] btns = {"Sửa lỗi", "nhắc lại", "Tiếp"};
        for (String txt : btns) {
            JButton b = new JButton(txt);
            b.setFont(new Font("Arial", Font.PLAIN, 18));

            if (txt.equals("Tiếp")) {
                b.addActionListener(e -> parent.sendCallNextRequest(departmentName));
            }
            
            if (txt.equals("Sửa lỗi")) {
                b.addActionListener(e -> handleErrorCorrection());
            }
            
            if (txt.equals("nhắc lại")) {
                b.addActionListener(e -> parent.sendRepeatCallRequest(departmentName));
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

    private void handleErrorCorrection() {
        System.out.println("Debug - handleErrorCorrection được gọi từ TicketPanel");
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        
        System.out.println("Debug - Số màn hình: " + screens.length);
        
        if (screens.length < 2) {
            parent.updateStatus("Chỉ có một màn hình, không thể thực hiện sửa lỗi");
            System.out.println("Debug - Chỉ có một màn hình");
            return;
        }
        
        // Lấy bounds của màn hình 1 và màn hình 2
        Rectangle screen1Bounds = screens[0].getDefaultConfiguration().getBounds();
        Rectangle screen2Bounds = screens[1].getDefaultConfiguration().getBounds();
        
        System.out.println("Debug - Màn hình 1: " + screen1Bounds);
        System.out.println("Debug - Màn hình 2: " + screen2Bounds);
        
        // Xử lý di chuyển tất cả cửa sổ từ màn hình 2 về màn hình 1
        processWindowMovementToScreen1(screen1Bounds, screen2Bounds);
    }

    private void processWindowMovementToScreen1(Rectangle screen1Bounds, Rectangle screen2Bounds) {
        // Lấy tất cả các cửa sổ hiện tại
        Window[] allWindows = Window.getWindows();
        int movedCount = 0;
        
        System.out.println("Debug - Tổng số cửa sổ Swing tìm thấy: " + allWindows.length);
        
        // Di chuyển các cửa sổ Swing NHƯNG bỏ qua TicketDisplayWindow
        for (Window window : allWindows) {
            if (window.isShowing() && window != parent && !isTicketDisplayWindow(window)) {
                Rectangle windowBounds = window.getBounds();
                System.out.println("Debug - Kiểm tra cửa sổ Swing: " + window.getClass().getSimpleName() + " tại " + windowBounds);
                
                // Kiểm tra xem cửa sổ có nằm trên màn hình 2 không
                boolean isOnScreen2 = (windowBounds.x >= screen2Bounds.x && 
                                     windowBounds.x < screen2Bounds.x + screen2Bounds.width) ||
                                    (windowBounds.x < screen1Bounds.x);
                
                if (isOnScreen2) {
                    moveWindowToScreen1(window, windowBounds, screen1Bounds, screen2Bounds);
                    movedCount++;
                }
            }
        }
        
        // Sử dụng native methods để di chuyển các ứng dụng khác
        moveNativeWindowsToScreen1(screen1Bounds, screen2Bounds);
        
        // KHÔNG XỬ LÝ TicketDisplayWindow - để yên hoàn toàn
        
        System.out.println("Debug - Đã di chuyển " + movedCount + " cửa sổ Swing (bỏ qua TicketDisplayWindow)");
        parent.updateStatus("Đã sửa lỗi: di chuyển ứng dụng về màn hình 1 (không can thiệp màn hình hiển thị)");
    }

    private boolean isTicketDisplayWindow(Window window) {
        if (!(window instanceof JFrame)) {
            return false;
        }
        
        JFrame frame = (JFrame) window;
        String title = frame.getTitle();
        
        // Kiểm tra nhiều pattern để đảm bảo bỏ qua TicketDisplayWindow
        return title != null && (
            title.contains("ỦY BAN NHÂN DÂN") ||
            title.contains("PHƯỜNG") ||
            title.contains("NÊNH") ||
            title.contains("TicketDisplay") ||
            title.toLowerCase().contains("ticket")
        );
    }

    private void moveWindowToScreen1(Window window, Rectangle windowBounds, Rectangle screen1Bounds, Rectangle screen2Bounds) {
        System.out.println("Debug - Di chuyển cửa sổ từ màn hình 2 về màn hình 1");
        
        // Tính toán vị trí mới trên màn hình 1
        int relativeX, relativeY;
        
        if (windowBounds.x < 0) {
            relativeX = windowBounds.x - screen2Bounds.x;
            relativeY = windowBounds.y - screen2Bounds.y;
        } else {
            relativeX = windowBounds.x - screen2Bounds.x;
            relativeY = windowBounds.y - screen2Bounds.y;
        }
        
        int newX = screen1Bounds.x + Math.max(0, relativeX);
        int newY = screen1Bounds.y + Math.max(0, relativeY);
        
        // Đảm bảo cửa sổ không ra ngoài màn hình 1
        if (newX + windowBounds.width > screen1Bounds.x + screen1Bounds.width) {
            newX = screen1Bounds.x + screen1Bounds.width - windowBounds.width - 50;
        }
        if (newY + windowBounds.height > screen1Bounds.y + screen1Bounds.height) {
            newY = screen1Bounds.y + screen1Bounds.height - windowBounds.height - 50;
        }
        
        newX = Math.max(screen1Bounds.x, Math.min(newX, screen1Bounds.x + screen1Bounds.width - 100));
        newY = Math.max(screen1Bounds.y, Math.min(newY, screen1Bounds.y + screen1Bounds.height - 100));
        
        System.out.println("Debug - Vị trí mới: " + newX + ", " + newY);
        window.setLocation(newX, newY);
        window.toFront();
        
        System.out.println("Debug - Đã di chuyển cửa sổ: " + window.getClass().getSimpleName());
    }

    private void moveNativeWindowsToScreen1(Rectangle screen1Bounds, Rectangle screen2Bounds) {
        System.out.println("Debug - Đang thực hiện di chuyển các ứng dụng native...");
        
        JDialog progressDialog = new JDialog((JFrame) parent, "Đang di chuyển ứng dụng", false);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(parent);
        
        JPanel content = new JPanel(new BorderLayout());
        JLabel message = new JLabel("<html><center>Đang di chuyển các ứng dụng về màn hình 1...<br>Vui lòng đợi...</center></html>", SwingConstants.CENTER);
        content.add(message, BorderLayout.CENTER);
        progressDialog.setContentPane(content);
        progressDialog.setVisible(true);
        
        new Thread(() -> {
            try {
                moveWindowsUsingWinAPI(screen1Bounds, screen2Bounds);
            } finally {
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    parent.updateStatus("Hoàn thành di chuyển ứng dụng về màn hình 1");
                });
            }
        }).start();
    }

    private void moveWindowsUsingWinAPI(Rectangle screen1Bounds, Rectangle screen2Bounds) {
        try {
            String script = createPowerShellScript(screen1Bounds, screen2Bounds);
            
            java.io.File tempScript = java.io.File.createTempFile("moveWindows", ".ps1");
            tempScript.deleteOnExit();
            
            try (java.io.FileWriter writer = new java.io.FileWriter(tempScript)) {
                writer.write(script);
            }
            
            ProcessBuilder pb = new ProcessBuilder(
                "powershell.exe", 
                "-ExecutionPolicy", "Bypass",
                "-File", tempScript.getAbsolutePath()
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            System.out.println("Debug - PowerShell script completed with exit code: " + exitCode);
            
            // Đọc output
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("PowerShell Output: " + line);
                }
            }
            
            // Đọc error output
            try (java.io.BufferedReader errorReader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.out.println("PowerShell Error: " + line);
                }
            }
            
            if (exitCode != 0) {
                System.out.println("Debug - PowerShell script failed, using Robot fallback");
                moveWindowsSimpleMethod(screen1Bounds, screen2Bounds);
            }
            
        } catch (Exception e) {
            System.out.println("Debug - Lỗi khi chạy PowerShell script: " + e.getMessage());
            e.printStackTrace();
            moveWindowsSimpleMethod(screen1Bounds, screen2Bounds);
        }
    }

    private String createPowerShellScript(Rectangle screen1Bounds, Rectangle screen2Bounds) {
        return String.format("""
            # PowerShell script to move windows from screen 2 to screen 1
            try {
                Add-Type @"
                using System;
                using System.Runtime.InteropServices;
                using System.Diagnostics;
                
                public class Win32 {
                    [DllImport("user32.dll")]
                    public static extern bool EnumWindows(EnumWindowsProc enumProc, IntPtr lParam);
                    
                    [DllImport("user32.dll")]
                    public static extern bool IsWindowVisible(IntPtr hWnd);
                    
                    [DllImport("user32.dll")]
                    public static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);
                    
                    [DllImport("user32.dll")]
                    public static extern bool SetWindowPos(IntPtr hWnd, IntPtr hWndInsertAfter, int X, int Y, int cx, int cy, uint uFlags);
                    
                    [DllImport("user32.dll")]
                    public static extern int GetWindowTextLength(IntPtr hWnd);
                    
                    [DllImport("user32.dll")]
                    public static extern int GetWindowText(IntPtr hWnd, System.Text.StringBuilder lpString, int nMaxCount);
                    
                    public const uint SWP_NOSIZE = 0x0001;
                    public const uint SWP_NOZORDER = 0x0004;
                    
                    public delegate bool EnumWindowsProc(IntPtr hWnd, IntPtr lParam);
                    
                    public struct RECT {
                        public int Left, Top, Right, Bottom;
                    }
                }
"@
            } catch {
                Write-Host "Error adding Win32 type: $_"
                exit 1
            }

            $screen1X = %d
            $screen1Y = %d
            $screen1Width = %d
            $screen1Height = %d
            $screen2X = %d
            $screen2Y = %d
            $screen2Width = %d
            $screen2Height = %d

            $movedCount = 0

            try {
                $enumProc = {
                    param($hWnd, $lParam)
                    
                    try {
                        if ([Win32]::IsWindowVisible($hWnd)) {
                            $rect = New-Object Win32+RECT
                            if ([Win32]::GetWindowRect($hWnd, [ref]$rect)) {
                                $windowX = $rect.Left
                                $windowY = $rect.Top
                                $windowWidth = $rect.Right - $rect.Left
                                $windowHeight = $rect.Bottom - $rect.Top
                                
                                if ($windowWidth -lt 100 -or $windowHeight -lt 50) {
                                    return $true
                                }
                                
                                $windowTitle = ""
                                $titleLength = [Win32]::GetWindowTextLength($hWnd)
                                if ($titleLength -gt 0) {
                                    $title = New-Object System.Text.StringBuilder($titleLength + 1)
                                    [Win32]::GetWindowText($hWnd, $title, $title.Capacity)
                                    $windowTitle = $title.ToString()
                                }
                                
                                # BỎ QUA TicketDisplayWindow - KIỂM TRA CHÍNH XÁC
                                if ($windowTitle -match ".*Y BAN.*" -or 
                                    $windowTitle -match ".*NHAN DAN.*" -or
                                    $windowTitle -match ".*PHU.*NG.*" -or 
                                    $windowTitle -match ".*NENH.*" -or
                                    $windowTitle -like "*TicketDisplay*" -or
                                    $windowTitle -like "*ticket*" -or
                                    $windowTitle -eq "ỦY BAN NHÂN DÂN PHƯỜNG NÊNH") {
                                    Write-Host "*** SKIPPING TICKET DISPLAY WINDOW (TUYỆT ĐỐI BỎ QUA): $windowTitle"
                                    return $true
                                }
                                
                                $isOnScreen2 = (($windowX -ge $screen2X -and $windowX -lt ($screen2X + $screen2Width)) -or ($windowX -lt $screen1X))
                                
                                if ($isOnScreen2) {
                                    Write-Host "Moving: $windowTitle"
                                    
                                    $relativeX = $windowX - $screen2X
                                    $relativeY = $windowY - $screen2Y
                                    
                                    $newX = $screen1X + [Math]::Max(0, $relativeX)
                                    $newY = $screen1Y + [Math]::Max(0, $relativeY)
                                    
                                    if (($newX + $windowWidth) -gt ($screen1X + $screen1Width)) {
                                        $newX = $screen1X + $screen1Width - $windowWidth - 50
                                    }
                                    if (($newY + $windowHeight) -gt ($screen1Y + $screen1Height)) {
                                        $newY = $screen1Y + $screen1Height - $windowHeight - 50
                                    }
                                    
                                    $newX = [Math]::Max($screen1X, $newX)
                                    $newY = [Math]::Max($screen1Y, $newY)
                                    
                                    if ([Win32]::SetWindowPos($hWnd, [IntPtr]::Zero, $newX, $newY, 0, 0, [Win32]::SWP_NOSIZE -bor [Win32]::SWP_NOZORDER)) {
                                        $script:movedCount++
                                        Write-Host "Moved to: $newX, $newY"
                                    } else {
                                        Write-Host "Failed to move: $windowTitle"
                                    }
                                } else {
                                    Write-Host "Window not on screen 2: $windowTitle"
                                }
                            }
                        }
                    } catch {
                        Write-Host "Error processing window: $_"
                    }
                    return $true
                }

                [Win32]::EnumWindows($enumProc, [IntPtr]::Zero)
                Write-Host "SUCCESS: Total windows moved: $movedCount"
                Write-Host "*** TicketDisplayWindow was ABSOLUTELY PROTECTED and NOT MOVED ***"
                exit 0
            } catch {
                Write-Host "Error in main script: $_"
                exit 1
            }
            """, 
            screen1Bounds.x, screen1Bounds.y, screen1Bounds.width, screen1Bounds.height,
            screen2Bounds.x, screen2Bounds.y, screen2Bounds.width, screen2Bounds.height);
    }

    private void moveWindowsSimpleMethod(Rectangle screen1Bounds, Rectangle screen2Bounds) {
        System.out.println("Debug - Sử dụng phương pháp đơn giản với Robot");
        
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(100);
            
            for (int i = 0; i < 15; i++) {
                robot.mouseMove(screen2Bounds.x + screen2Bounds.width/2, screen2Bounds.y + screen2Bounds.height/2);
                robot.mousePress(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseRelease(java.awt.event.InputEvent.BUTTON1_DOWN_MASK);
                robot.delay(100);
                
                robot.keyPress(java.awt.event.KeyEvent.VK_ALT);
                robot.keyPress(java.awt.event.KeyEvent.VK_TAB);
                robot.delay(50);
                robot.keyRelease(java.awt.event.KeyEvent.VK_TAB);
                robot.keyRelease(java.awt.event.KeyEvent.VK_ALT);
                robot.delay(200);
                
                robot.keyPress(java.awt.event.KeyEvent.VK_WINDOWS);
                robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
                robot.keyPress(java.awt.event.KeyEvent.VK_LEFT);
                robot.delay(100);
                robot.keyRelease(java.awt.event.KeyEvent.VK_LEFT);
                robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
                robot.keyRelease(java.awt.event.KeyEvent.VK_WINDOWS);
                robot.delay(300);
                
                System.out.println("Debug - Completed iteration " + (i+1));
            }
            
        } catch (Exception e) {
            System.out.println("Debug - Lỗi robot fallback: " + e.getMessage());
        }
    }

    // Hàm để cập nhật dữ liệu từ server
    public void addTicket(String code, String status) {
        tableModel.addRow(new Object[]{code, status});
    }

    public void clearTickets() {
        tableModel.setRowCount(0);
    }
}