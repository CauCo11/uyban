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
        new Department("Tài chính", "1"),
        new Department("Tư pháp dân tộc tôn giáo", "2"),
        new Department("Giáo dục", "3"),
        new Department("Y tế", "4"),
        new Department("Lao động", "5"),
        new Department("Tư pháp", "6"),
        new Department("Văn hóa", "7"),
        new Department("Giao thông", "8"),
        new Department("Môi trường", "9"),
        new Department("Kế hoạch", "10"),
        new Department("Xây dựng", "11")
    );
    
    // For backward compatibility
    public static final List<String> DEPARTMENTS = Arrays.asList(
        "Tài chính", "Tư pháp dân tộc tôn giáo", "Giáo dục", "Y tế", "Lao động",
        "Tư pháp", "Văn hóa", "Giao thông", "Môi trường", "Kế hoạch", "Xây dựng"
    );
    
    public static String getDepartmentCounter(String departmentName) {
        return DEPARTMENT_LIST.stream()
            .filter(d -> d.name.equals(departmentName))
            .map(d -> d.counter)
            .findFirst()
            .orElse("00");
    }
}