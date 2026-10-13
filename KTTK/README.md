# BÁO CÁO BÀI TẬP LỚN: HỆ THỐNG CHAT NHÓM ĐA PHÒNG QUA TCP (JAVA SOCKET)

---

## 1. GIỚI THIỆU TỔNG QUAN

Dự án xây dựng một ứng dụng Chat Nhóm Đa Phòng (Multi-Room Group Chat) dựa trên giao thức **TCP Socket** và kỹ thuật **Đa luồng (Multi-threading)** trong Java. 

Hệ thống cho phép nhiều người dùng (Client) kết nối đồng thời tới một Server trung tâm, tham gia vào các phòng chat riêng biệt, tự tạo phòng mới hoặc gửi tin nhắn riêng (Private Message) giữa các cá nhân trong cùng một phòng.

---

## 2. KIẾN TRÚC VÀ CẤU TRÚC THƯ MỤC

Dự án được tổ chức trong package `chat_room` với cấu trúc như sau:

```plaintext
src/
 └── chat_room/
      ├── ChatConstants.java
      ├── MultiRoomChatServer.java
      └── MultiRoomChatClient.java
```

### Chi tiết các lớp (Classes):
* **`ChatConstants.java`**: Chứa toàn bộ hằng số cấu hình hệ thống (Port `12345`, danh sách phòng mặc định, cú pháp câu lệnh `/pm`, `/list`, `/quit`, `/exit`, định dạng thời gian).
* **`MultiRoomChatServer.java`**: Server TCP lắng nghe kết nối, quản lý các luồng client (`ClientHandler`), xử lý logic tạo/chuyển phòng chat, gửi tin nhắn nhóm và tin nhắn riêng.
* **`MultiRoomChatClient.java`**: Client kết nối tới Server, khởi tạo luồng riêng để lắng nghe tin nhắn từ Server và đọc dữ liệu từ bàn phím để gửi đi.

---

## 3. CÁC TÍNH NĂNG CHÍNH VÀ ĐÁP ỨNG YÊU CẦU

| STT | Yêu cầu bài toán | Giải pháp & Logic thực thi trong Code |
| :---: | :--- | :--- |
| **1** | Nhập Tên & Kết nối Server | Client gửi tên đăng nhập ngay khi kết nối. Server lưu thông tin vào `ClientHandler`. |
| **2** | Hiển thị danh sách phòng | Server quản lý danh sách phòng bằng `ConcurrentHashMap<String, List<ClientHandler>>` và tự động in danh sách kèm số lượng người online. |
| **3** | Tự đề xuất & Tạo phòng mới | Hàm `rooms.putIfAbsent()` tự động khởi tạo phòng mới nếu người dùng nhập tên phòng chưa có trong hệ thống. |
| **4** | Cô lập tin nhắn theo phòng | Hàm `broadcastToRoom()` chỉ gửi tin nhắn cho các client thuộc danh sách người dùng trong phòng hiện tại (`currentRoom`). |
| **5** | Đổi phòng bằng lệnh `/quit` | Nhận lệnh `/quit`, client thực thi `leaveCurrentRoom()` và quay trở lại vòng lặp chọn phòng. |
| **6** | Ngắt kết nối bằng lệnh `/exit` | Nhận lệnh `/exit`, server đóng socket, giải phóng luồng và dọn dẹp bộ nhớ an toàn. |
| **7** | *Tính năng nâng cao (Tự chọn)* | Hỗ trợ gửi tin nhắn riêng ` /pm <tên_user> <nội_dung>` và xem danh sách thành viên `/list`. |

---

## 4. HƯỚNG DẪN BIÊN DỊCH VÀ CHẠY CHƯƠNG TRÌNH

### 4.1. Chạy trên VS Code (Visual Studio Code)
1. Cài đặt **Extension Pack for Java** trên VS Code.
2. Mở thư mục dự án (đảm bảo cấu trúc gói `chat_room` chính xác).
3. Mở file `MultiRoomChatServer.java` -> Bấm **Run** để khởi động Server.
4. Mở file `MultiRoomChatClient.java` -> Bấm **Run** để khởi động Client thứ nhất.
5. Tiếp tục bấm **Run** ở `MultiRoomChatClient.java` để mở thêm nhiều Client thử nghiệm chat qua lại.

### 4.2. Chạy bằng Dòng lệnh (Terminal / CMD)
```bash
# Biên dịch tất cả các file Java
javac -d bin src/chat_room/*.java

# Chạy Server
java -cp bin chat_room.MultiRoomChatServer

# Mở các cửa sổ Terminal mới để chạy Client
java -cp bin chat_room.MultiRoomChatClient
```

---

## 5. KẾT LUẬN

Chương trình đáp ứng chính xác và đầy đủ các yêu cầu về một ứng dụng chat nhóm đa phòng sử dụng Java Socket qua TCP. Hệ thống xử lý đa luồng an toàn, cô lập dữ liệu theo từng phòng chat và có khả năng mở rộng tốt.
