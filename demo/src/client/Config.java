// NẾNH
package client;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static class Department {
        public final String name;
        public final String counter;
        
        public Department(String name, String counter) {
            this.name = name;
            this.counter = counter;
        }
    }
    
    public static final List<Department> DEPARTMENT_LIST = Arrays.asList(
        new Department("Tư pháp - Hộ tịch, Thanh tra", "1"),
        new Department("Kiểm soát thủ tục hành chính", "2"),
        new Department("Nội vụ, Y tế, Văn hóa - Xã hội, Khoa học - Công nghệ, Giáo dục - Đào tạo, Tôn giáo", "3"),
        new Department("Đất đai, Nông nghiệp, Môi trường", "4"),
        new Department("Xây dựng", "5"),
        new Department("Công thương, Tài chính", "6")
    );
    
    // For backward compatibility
    public static final List<String> DEPARTMENTS = Arrays.asList(
        "Tư pháp - Hộ tịch, Thanh tra", "Kiểm soát thủ tục hành chính", "Nội vụ, Y tế, Văn hóa - Xã hội, Khoa học - Công nghệ, Giáo dục - Đào tạo, Tôn giáo", "Đất đai, Nông nghiệp, Môi trường", "Xây dựng", "Công thương, Tài chính"
    );
    
    public static String getDepartmentCounter(String departmentName) {
        return DEPARTMENT_LIST.stream()
            .filter(d -> d.name.equals(departmentName))
            .map(d -> d.counter)
            .findFirst()
            .orElse("00");
    }
}