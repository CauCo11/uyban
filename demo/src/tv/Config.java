package tv;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static final List<String> DEPARTMENTS = Arrays.asList(
        "Tư pháp - Hộ tịch, Thanh tra",
        "Nội vụ - Y tế - Giáo dục và đào tạo", 
        "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin",
        "Xây Dựng",
        "Nông nghiệp - Môi Trường",
        "Công thương, Tài chính"
    );

    public static String getDepartmentCounter(String departmentName) {
        switch (departmentName) {
            case "Tư pháp - Hộ tịch, Thanh tra":
                return "1";
            case "Nội vụ - Y tế - Giáo dục và đào tạo":
                return "2";
            case "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin":
                return "3";
            case "Xây Dựng":
                return "4";
            case "Nông nghiệp - Môi Trường":
                return "5";
            case "Công thương, Tài chính":
                return "6";
            default:
                return "0";
        }
    }
}
