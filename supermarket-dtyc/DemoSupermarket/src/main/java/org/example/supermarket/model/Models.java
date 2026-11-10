package org.example.supermarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cac ban ghi (record) anh xa tu cac bang trong CSDL.
 * Gom chung 1 file cho gon - moi record tuong ung 1 bang (hoac 1 cau truy van join).
 */
public final class Models {
    private Models() {}

    /** Bang TAIKHOAN */
    public record TaiKhoan(String email, String hoTen, String loaiTaiKhoan,
                           String trangThai, String soDienThoai) {}

    /** Bang DIACHI */
    public record DiaChi(int stt, String email, String tenNguoiNhan, String soDienThoai,
                         String tinh, String huyen, String xa, String diaChiChiTiet, boolean macDinh) {
        public String diaChiDayDu() {
            return diaChiChiTiet + ", " + xa + ", " + huyen + ", " + tinh;
        }
    }

    /** Bang SP (kem so serial con trong kho) */
    public record SanPham(String maSanPham, String ten, BigDecimal giaBan, int soLuong,
                          String donViTinh, String trangThai, String moTa, String hinhAnh,
                          String tenLoai, String tenThuongHieu,
                          int serialTrongKho, int serialDaBan, int serialLoi) {
        /** So luong co the ban theo ton kho */
        public int coTheBan() {
            return soLuong;
        }
    }

    /** Bang PNK (kem ten NV, NCC, tong tien) */
    public record PhieuNhap(int stt, LocalDateTime ngayGio, String trangThai, String ghiChu,
                            String emailNhanVien, String tenNhanVien,
                            String emailNhaCungCap, String tenNhaCungCap,
                            int soMatHang, int tongSoLuong, BigDecimal tongTien) {}

    /** Bang CTPNK */
    public record CtPhieuNhap(String maSanPham, String tenSanPham, String donViTinh,
                              int soLuong, BigDecimal donGiaNhap) {
        public BigDecimal thanhTien() {
            return donGiaNhap.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    /** Bang DON (kem ten KH, NV) */
    public record Don(int stt, LocalDateTime ngayGio, String trangThai, BigDecimal tongTien,
                      String tenNguoiNhan, String sdtNhan, String diaChiNhan,
                      String phuongThucThanhToan, String trangThaiThanhToan, String ghiChu,
                      String donViVanChuyen, String maVanDon,
                      String emailKhachHang, String tenKhachHang,
                      String emailNhanVien, String tenNhanVien, int soMatHang) {}

    /** Bang CTDON */
    public record CtDon(String maSanPham, String tenSanPham, String donViTinh,
                        int soLuong, BigDecimal donGiaBan) {
        public BigDecimal thanhTien() {
            return donGiaBan.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    /** Bang SPCUTHE */
    public record SpCuThe(int stt, String tt, String maDinhDanh, String maSanPham,
                          String tenSanPham, int sttPnk, Integer sttDon) {}

    /** Ket qua tra cuu 1 serial: truy vet tu phieu nhap -> don ban */
    public record TruyVetSerial(SpCuThe serial, PhieuNhap phieuNhap, BigDecimal donGiaNhap,
                                Don don, BigDecimal donGiaBan) {}

    /** So lieu tong quan cho Dashboard */
    public record ThongKe(int soSanPham, long tongTonKho, int donChoXacNhan,
                          BigDecimal doanhThu, int soPhieuNhap, BigDecimal tongTienNhap) {}
}
