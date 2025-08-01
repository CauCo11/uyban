// package server;

// import java.util.Arrays;
// import java.util.List;

// public class Config {
//     public static class Department {
//         public final String name;
//         public final String counter;
        
//         public Department(String name, String counter) {
//             this.name = name;
//             this.counter = counter;
//         }
//     }
    
//     public static final List<Department> DEPARTMENT_LIST = Arrays.asList(
//         new Department("Tư pháp - Hộ tịch, Thanh tra", "1"),
//         new Department("Nội vụ - Y tế - Giáo dục và đào tạo", "2"),
//         new Department("Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin", "3"),
//         new Department("Xây Dựng", "4"),
//         new Department("Xây dựng", "5"),
//         new Department("Công thương, Tài chính", "6")
//     );
    
//     // For backward compatibility
//     public static final List<String> DEPARTMENTS = Arrays.asList(
//         "Tư pháp - Hộ tịch, Thanh tra", "Nội vụ - Y tế - Giáo dục và đào tạo", "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin", "Xây Dựng", "Xây dựng", "Công thương, Tài chính"
//     );
    
    
//     public static String getDepartmentCounter(String departmentName) {
//         return DEPARTMENT_LIST.stream()
//             .filter(d -> d.name.equals(departmentName))
//             .map(d -> d.counter)
//             .findFirst()
//             .orElse("00");
//     }
// }
package server;

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
        new Department("Nội vụ - Y tế - Giáo dục và đào tạo", "2"),
        new Department("Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin", "3"),
        new Department("Xây Dựng", "4"),
        new Department("Nông nghiệp - Môi Trường", "5"),
        new Department("Công thương, Tài chính", "6")
    );
    
    // For backward compatibility
    public static final List<String> DEPARTMENTS = Arrays.asList(
        "Tư pháp - Hộ tịch, Thanh tra", "Nội vụ - Y tế - Giáo dục và đào tạo", "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin", "Xây Dựng", "Nông nghiệp - Môi Trường", "Công thương, Tài chính"
    );
    
    
    public static String getDepartmentCounter(String departmentName) {
        return DEPARTMENT_LIST.stream()
            .filter(d -> d.name.equals(departmentName))
            .map(d -> d.counter)
            .findFirst()
            .orElse("00");
    }
}