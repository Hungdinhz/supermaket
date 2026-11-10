package org.example.supermarket.repository;

import org.example.supermarket.model.Models.DiaChi;
import org.example.supermarket.model.Models.TaiKhoan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy van bang TAIKHOAN va DIACHI */
@Repository
public class TaiKhoanRepository {
    private final JdbcTemplate jdbc;

    public TaiKhoanRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<TaiKhoan> TK_MAPPER = (rs, i) -> new TaiKhoan(
            rs.getString("email"), rs.getString("ho_ten"), rs.getString("loai_tai_khoan"),
            rs.getString("trang_thai"), rs.getString("so_dien_thoai"));

    private static final RowMapper<DiaChi> DC_MAPPER = (rs, i) -> new DiaChi(
            rs.getInt("stt"), rs.getString("email"), rs.getString("ten_nguoi_nhan"),
            rs.getString("so_dien_thoai"), rs.getString("tinh"), rs.getString("huyen"),
            rs.getString("xa"), rs.getString("dia_chi_chi_tiet"), rs.getBoolean("mac_dinh"));

    /** Lay danh sach tai khoan theo loai: NHAN_VIEN, NHA_CUNG_CAP, KHACH_HANG, ADMIN */
    public List<TaiKhoan> findByLoai(String... loai) {
        String in = String.join(",", java.util.Collections.nCopies(loai.length, "?"));
        return jdbc.query("""
                SELECT email, ho_ten, loai_tai_khoan, trang_thai, so_dien_thoai
                FROM taikhoan
                WHERE loai_tai_khoan IN (%s)
                ORDER BY ho_ten
                """.formatted(in), TK_MAPPER, (Object[]) loai);
    }

    public Optional<TaiKhoan> findByEmail(String email) {
        return jdbc.query("""
                SELECT email, ho_ten, loai_tai_khoan, trang_thai, so_dien_thoai
                FROM taikhoan WHERE email = ?
                """, TK_MAPPER, email).stream().findFirst();
    }

    /** So dia chi cua 1 tai khoan (dia chi mac dinh len dau) */
    public List<DiaChi> findDiaChi(String email) {
        return jdbc.query("""
                SELECT * FROM diachi
                WHERE email = ?
                ORDER BY mac_dinh DESC, stt
                """, DC_MAPPER, email);
    }
}
