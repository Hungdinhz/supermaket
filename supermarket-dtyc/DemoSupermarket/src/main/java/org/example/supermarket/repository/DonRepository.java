package org.example.supermarket.repository;

import org.example.supermarket.model.Models.CtDon;
import org.example.supermarket.model.Models.Don;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Truy van bang DON (Don hang) va CTDON (Chi tiet don) */
@Repository
public class DonRepository {
    private final JdbcTemplate jdbc;

    public DonRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT d.*, kh.ho_ten AS ten_khach_hang, nv.ho_ten AS ten_nhan_vien,
                   (SELECT COUNT(*) FROM ctdon ct WHERE ct.stt_don = d.stt) AS so_mat_hang
            FROM don d
            JOIN taikhoan kh      ON kh.email = d.email_khach_hang
            LEFT JOIN taikhoan nv ON nv.email = d.email_nhan_vien
            """;

    private static final RowMapper<Don> MAPPER = (rs, i) -> new Don(
            rs.getInt("stt"), rs.getTimestamp("ngay_gio").toLocalDateTime(),
            rs.getString("trang_thai"), rs.getBigDecimal("tong_tien"),
            rs.getString("ten_nguoi_nhan"), rs.getString("sdt_nhan"), rs.getString("dia_chi_nhan"),
            rs.getString("phuong_thuc_thanh_toan"), rs.getString("trang_thai_thanh_toan"),
            rs.getString("ghi_chu"), rs.getString("don_vi_van_chuyen"), rs.getString("ma_van_don"),
            rs.getString("email_khach_hang"), rs.getString("ten_khach_hang"),
            rs.getString("email_nhan_vien"), rs.getString("ten_nhan_vien"), rs.getInt("so_mat_hang"));

    public List<Don> search(String trangThai, String keyword) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1 ");
        List<Object> args = new ArrayList<>();
        if (trangThai != null && !trangThai.isBlank()) {
            sql.append(" AND d.trang_thai = ? ");
            args.add(trangThai);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (kh.ho_ten ILIKE ? OR d.ten_nguoi_nhan ILIKE ? OR d.sdt_nhan ILIKE ? OR CAST(d.stt AS TEXT) = ?) ");
            String k = "%" + keyword.trim() + "%";
            args.add(k);
            args.add(k);
            args.add(k);
            args.add(keyword.trim());
        }
        sql.append(" ORDER BY d.stt DESC");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    public List<Don> findGanNhat(int limit) {
        return jdbc.query(SELECT + " ORDER BY d.stt DESC LIMIT ?", MAPPER, limit);
    }

    public Optional<Don> findById(int stt) {
        return jdbc.query(SELECT + " WHERE d.stt = ?", MAPPER, stt).stream().findFirst();
    }

    /** Dem so don theo tung trang thai (de hien so tren cac tab) */
    public Map<String, Integer> demTheoTrangThai() {
        Map<String, Integer> map = new LinkedHashMap<>();
        jdbc.query("SELECT trang_thai, COUNT(*) FROM don GROUP BY trang_thai",
                rs -> { map.put(rs.getString(1), rs.getInt(2)); });
        return map;
    }

    public List<CtDon> findChiTiet(int sttDon) {
        return jdbc.query("""
                SELECT ct.ma_san_pham, sp.ten, sp.don_vi_tinh, ct.so_luong, ct.don_gia_ban
                FROM ctdon ct JOIN sp ON sp.ma_san_pham = ct.ma_san_pham
                WHERE ct.stt_don = ?
                ORDER BY sp.ten
                """, (rs, i) -> new CtDon(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getBigDecimal(5)), sttDon);
    }

    /** INSERT DON, tra ve so don (stt) vua sinh */
    public int insertDon(String trangThai, BigDecimal tongTien, String tenNguoiNhan, String sdtNhan,
                         String diaChiNhan, String phuongThuc, String trangThaiThanhToan,
                         String ghiChu, String emailKhachHang) {
        return jdbc.queryForObject("""
                INSERT INTO don (ngay_gio, trang_thai, tong_tien, ten_nguoi_nhan, sdt_nhan, dia_chi_nhan,
                                 phuong_thuc_thanh_toan, trang_thai_thanh_toan, ghi_chu, email_khach_hang)
                VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING stt
                """, Integer.class, trangThai, tongTien, tenNguoiNhan, sdtNhan, diaChiNhan,
                phuongThuc, trangThaiThanhToan, ghiChu, emailKhachHang);
    }

    public void insertChiTiet(int sttDon, String maSanPham, int soLuong, BigDecimal donGiaBan) {
        jdbc.update("""
                INSERT INTO ctdon (stt_don, ma_san_pham, so_luong, don_gia_ban)
                VALUES (?, ?, ?, ?)
                """, sttDon, maSanPham, soLuong, donGiaBan);
    }

    public void xacNhan(int stt, String trangThai, String emailNhanVien) {
        jdbc.update("UPDATE don SET trang_thai = ?, email_nhan_vien = ? WHERE stt = ?",
                trangThai, emailNhanVien, stt);
    }

    public void giaoHang(int stt, String trangThai, String donViVanChuyen, String maVanDon) {
        jdbc.update("UPDATE don SET trang_thai = ?, don_vi_van_chuyen = ?, ma_van_don = ? WHERE stt = ?",
                trangThai, donViVanChuyen, maVanDon, stt);
    }

    public void updateTrangThai(int stt, String trangThai) {
        jdbc.update("UPDATE don SET trang_thai = ? WHERE stt = ?", trangThai, stt);
    }

    public void updateThanhToan(int stt, String trangThaiThanhToan) {
        jdbc.update("UPDATE don SET trang_thai_thanh_toan = ? WHERE stt = ?", trangThaiThanhToan, stt);
    }

    public BigDecimal doanhThu(String trangThaiHoanThanh) {
        return jdbc.queryForObject("SELECT COALESCE(SUM(tong_tien), 0) FROM don WHERE trang_thai = ?",
                BigDecimal.class, trangThaiHoanThanh);
    }
}
