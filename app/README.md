💰 MoneyNote - Ứng Dụng Quản Lý Chi Tiêu Cá Nhân

MoneyNote là một ứng dụng di động giúp người dùng theo dõi và quản lý tài chính cá nhân một cách trực quan, dễ dàng. Ứng dụng được phát triển với công nghệ Android hiện đại nhất.

🌟 Tính Năng Chính

Nhập Liệu Nhanh: Dễ dàng thêm các giao dịch Thu/Chi với hệ thống danh mục trực quan.

Quản lý Tài khoản (Ví): Hỗ trợ theo dõi chi tiêu trên nhiều tài khoản khác nhau (Tiền mặt, Ngân hàng, v.v.).

Báo cáo Đồ họa: Hiển thị tổng quan Thu/Chi theo tháng với dữ liệu được nhóm theo danh mục.

Quản lý Ngân sách: Thiết lập hạn mức chi tiêu hàng tháng cho từng danh mục và theo dõi tiến độ qua thanh tiến trình.

Giao diện Hiện đại: Sử dụng Dark Theme (Chủ đề Tối) với Jetpack Compose (Material 3) mang lại trải nghiệm người dùng tối ưu.

🛠️ Công Nghệ & Kiến Trúc

Ngôn ngữ: Kotlin

UI Framework: Jetpack Compose (Material 3)

Kiến trúc: MVVM (Model - View - ViewModel)

Lưu trữ Dữ liệu: Room Database (dùng Coroutines và Flow để xử lý dữ liệu bất đồng bộ)

Điều hướng: Jetpack Navigation Compose

🚀 Cài Đặt và Khởi Chạy

Clone Repository:

git clone [DÁN_URL_REPOSITORY_CỦA_BẠN_VÀO_ĐÂY]


Mở Dự án: Mở thư mục dự án trong Android Studio.

Cấu hình Gradle: Đảm bảo Android Studio đã đồng bộ (Sync) các tệp Gradle thành công.

Chạy ứng dụng: Chọn một thiết bị hoặc máy ảo Android có API 24 (Android 7.0) trở lên và chạy ứng dụng.

Lưu ý quan trọng (Nếu là lần đầu chạy): Ứng dụng sử dụng Room Database và tự động tạo các tài khoản "Tiền mặt", "Ngân hàng" khi chạy lần đầu. Nếu bạn đã chạy thất bại trước đó, bạn cần vào Cài đặt ứng dụng trên thiết bị và chọn "Clear Data" (Xóa dữ liệu) để database được tạo lại chính xác.