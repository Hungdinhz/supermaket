package org.example.supermarket.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Du lieu nhan tu form tren trinh duyet (Spring tu dong binding theo ten input).
 */
public final class Forms {
    private Forms() {}

    /** 1 dong chi tiet (dung chung cho phieu nhap & don hang) */
    public static class Dong {
        private String maSanPham;
        private Integer soLuong;
        private BigDecimal donGia;

        public String getMaSanPham() { return maSanPham; }
        public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }
        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
        public BigDecimal getDonGia() { return donGia; }
        public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }

        public boolean isEmpty() {
            return (maSanPham == null || maSanPham.isBlank()) && soLuong == null && donGia == null;
        }
    }

    /** Form lap Phieu nhap kho */
    public static class PhieuNhapForm {
        private String emailNhanVien;
        private String emailNhaCungCap;
        private String ghiChu;
        private List<Dong> chiTiet = new ArrayList<>();

        public String getEmailNhanVien() { return emailNhanVien; }
        public void setEmailNhanVien(String v) { this.emailNhanVien = v; }
        public String getEmailNhaCungCap() { return emailNhaCungCap; }
        public void setEmailNhaCungCap(String v) { this.emailNhaCungCap = v; }
        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
        public List<Dong> getChiTiet() { return chiTiet; }
        public void setChiTiet(List<Dong> chiTiet) { this.chiTiet = chiTiet; }
    }

    /** Form tao Don hang */
    public static class DonHangForm {
        private String emailKhachHang;
        private String tenNguoiNhan;
        private String sdtNhan;
        private String diaChiNhan;
        private String phuongThucThanhToan = "COD";
        private String ghiChu;
        private List<Dong> chiTiet = new ArrayList<>();

        public String getEmailKhachHang() { return emailKhachHang; }
        public void setEmailKhachHang(String v) { this.emailKhachHang = v; }
        public String getTenNguoiNhan() { return tenNguoiNhan; }
        public void setTenNguoiNhan(String v) { this.tenNguoiNhan = v; }
        public String getSdtNhan() { return sdtNhan; }
        public void setSdtNhan(String v) { this.sdtNhan = v; }
        public String getDiaChiNhan() { return diaChiNhan; }
        public void setDiaChiNhan(String v) { this.diaChiNhan = v; }
        public String getPhuongThucThanhToan() { return phuongThucThanhToan; }
        public void setPhuongThucThanhToan(String v) { this.phuongThucThanhToan = v; }
        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
        public List<Dong> getChiTiet() { return chiTiet; }
        public void setChiTiet(List<Dong> chiTiet) { this.chiTiet = chiTiet; }
    }
}
