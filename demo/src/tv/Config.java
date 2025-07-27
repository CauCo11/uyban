package tv;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static final List<String> DEPARTMENTS = Arrays.asList(
        "Tư pháp - Hộ tịch, Thanh tra",
        "Kiểm soát thủ tục hành chính", 
        "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin",
        "Nông Nghiệp và Môi Trường",
        "Xây Dựng",
        "Công thương, Tài chính"
    );

    public static String getDepartmentCounter(String departmentName) {
        switch (departmentName) {
            case "Tư pháp - Hộ tịch, Thanh tra":
                return "1";
            case "Kiểm soát thủ tục hành chính":
                return "2";
            case "Dân Tộc - Tôn Giáo - Thi Đua Khen Thưởng - Văn Hóa - Khoa Học và Thông Tin":
                return "3";
            case "Nông Nghiệp và Môi Trường":
                return "4";
            case "Xây Dựng":
                return "5";
            case "Công thương, Tài chính":
                return "6";
            default:
                return "0";
        }
    }
}
