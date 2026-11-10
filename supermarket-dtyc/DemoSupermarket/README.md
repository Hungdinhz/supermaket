# FreshMart – Web Demo 2 Module (Nhập kho & Bán hàng)

Ứng dụng web chạy tại **`http://localhost:8088`** kết nối trực tiếp vào CSDL PostgreSQL `supermarket` (12 bảng theo sơ đồ ERD môn Đặc tả yêu cầu).

---

## 1. Cách chạy nhanh

### Cách 1 – Chạy trong IntelliJ IDEA (Khuyên dùng khi thi)
1. Mở thư mục `supermarket-dtyc/DemoSupermarket` trong IntelliJ IDEA.
2. Mở file [`src/main/java/org/example/supermarket/SupermarketApplication.java`](src/main/java/org/example/supermarket/SupermarketApplication.java).
3. Bấm nút **Run ▶️** ở cạnh hàm `main`.
4. Mở trình duyệt vào: **http://localhost:8088**

### Cách 2 – Chạy bằng file `.bat`
- Nháy đúp vào `run-demo.bat` trong thư mục `DemoSupermarket`.

> **Cấu hình DB:** nằm tại [`src/main/resources/application.yml`](src/main/resources/application.yml) (`jdbc:postgresql://localhost:5432/supermarket`, user `postgres`, password `123456`).
> Ứng dụng dùng `JdbcTemplate` (SQL thuần), **không dùng Hibernate `ddl-auto`** nên không bao giờ tự ý sửa cấu trúc bảng của bạn.

---

## 2. Hai Module Demo Chính & Liên Hệ ERD

| Module | Đường dẫn | Bảng ERD tham gia | Điểm nhấn nghiệp vụ |
|---|---|---|---|
| **Module 1: Nhập kho** | `/nhap-kho` | `PNK`, `CTPNK`, `SPCUTHE`, `SP`, `TAIKHOAN` (NV + NCC) | Lập phiếu nhập nhiều mặt hàng (thêm/xóa dòng động, tự tính tiền). Trong **1 transaction**: lưu `PNK` → lưu `CTPNK` → **tự động sinh mã định danh (`SPCUTHE`) cho từng đơn vị nhập** (`SN-{MaSP}-P{SttPNK}-0001...`) → cộng tồn kho `SP.so_luong`. Hỗ trợ hủy phiếu khi hàng chưa bán. |
| **Module 2: Bán hàng / Đơn hàng** | `/don-hang` | `DON`, `CTDON`, `SPCUTHE`, `SP`, `TAIKHOAN` (KH + NV), `DIACHI` | Chọn khách hàng → **tự tải sổ địa chỉ (`DIACHI`)** và điền sẵn địa chỉ mặc định. Lưu đơn ở trạng thái `CHO_XAC_NHAN` (`email_nhan_vien = NULL` đúng quan hệ `o\|..o{`). Khi **Nhân viên xác nhận đơn**: gán NV xử lý, **lấy serial `TRONG_KHO` theo FIFO (`FOR UPDATE`)** chuyển sang `DA_BAN` + gán `stt_don`, trừ `SP.so_luong`. Hỗ trợ bàn giao vận chuyển (`ma_van_don` UNIQUE), hoàn thành đơn, hoặc hủy đơn (hoàn lại tồn kho & trả serial về `TRONG_KHO`). |
| **Truy vết mã định danh** | `/tra-cuu` | `PNK` → `CTPNK` → `SPCUTHE` → `CTDON` → `DON` | Nhập hoặc bấm vào bất kỳ mã `SPCUTHE.ma_dinh_danh` nào để xem sơ đồ 3 bước: **Phiếu nhập gốc (NCC, ngày nhập, giá nhập)** → **Cá thể sản phẩm** → **Đơn hàng đã mua (khách hàng, người nhận, giá bán)**. |

---

## 3. Kịch Bản Demo Trực Tiếp Với Thầy (3–5 phút)

1. **Mở Dashboard (`/`):** Giới thiệu tổng quan số sản phẩm, tổng tồn kho, số phiếu nhập, đơn chờ xác nhận và doanh thu.
2. **Vào Module 1 – Nhập kho (`/nhap-kho/moi`):**
   - Chọn Nhân viên lập phiếu (`Hoàng Thu Trang`), Nhà cung cấp (`Công ty Thực phẩm Việt`).
   - Thêm 2 dòng sản phẩm, ví dụ nhập **5** đơn vị `SP001` và **3** đơn vị `SP005`. Thấy giao diện báo ngay tồn hiện tại → tồn sau nhập và tự tính thành tiền.
   - Bấm **Lưu phiếu nhập** → chuyển sang trang Chi tiết phiếu nhập: chỉ cho thầy thấy **8 mã định danh (`SPCUTHE`) vừa được tự động sinh** ở trạng thái `Trong kho`.
3. **Vào Module 2 – Đơn hàng (`/don-hang/moi`):**
   - Chọn khách hàng `Nguyễn Văn An` → chỉ cho thầy thấy **Sổ địa chỉ (`DIACHI`) tự động hiện ra** và tự điền tên/SĐT/địa chỉ mặc định.
   - Chọn mua **2** đơn vị `SP001` (đơn giá tự lấy từ `SP.gia_ban`). Bấm **Đặt hàng**.
   - Ở trang Chi tiết đơn: giải thích lúc mới đặt trạng thái là `CHO_XAC_NHAN`, chưa có nhân viên xử lý (`email_nhan_vien = NULL`) và chưa xuất serial.
   - Chọn nhân viên xử lý → bấm **Xác nhận đơn**: chỉ cho thầy thấy **2 mã định danh theo FIFO đã được gán vào đơn** (chuyển sang `Đã bán`) và tồn kho đã trừ 2.
   - Nhập đơn vị vận chuyển (`GHN`) + mã vận đơn → bấm **Bắt đầu giao** → bấm **Giao thành công** (đơn chuyển sang `HOAN_THANH` + `DA_THANH_TOAN`).
4. **Chốt điểm ấn tượng – Truy vết Serial (`/tra-cuu`):**
   - Bấm thẳng vào 1 mã định danh trong đơn vừa bán → màn hình hiện chuỗi truy vết 3 khối: **Phiếu nhập kho gốc → Cá thể sản phẩm → Đơn hàng đầu ra**, chứng minh toàn bộ liên kết khóa ngoại trên ERD hoạt động chặt chẽ.
