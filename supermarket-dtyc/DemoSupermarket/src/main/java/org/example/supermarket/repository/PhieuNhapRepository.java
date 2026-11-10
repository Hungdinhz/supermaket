package org.example.supermarket.repository;

import org.example.supermarket.model.Models.CtPhieuNhap;
import org.example.supermarket.model.Models.PhieuNhap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Truy van bang PNK (Phieu nhap kho) va CTPNK (Chi tiet phieu nhap) */
@Repository
public class PhieuNhapRepository {
    private final JdbcTemplate jdbc;

    public PhieuNhapRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT p.stt, p.ngay_gio, p.trang_thai, p.ghi_chu,
                   p.email_nhan_vien, nv.ho_ten AS ten_nhan_vien,
                   p.email_nha_cung_cap, ncc.ho_ten AS ten_nha_cung_cap,
                   COUNT(ct.ma_san_pham)                       AS so_mat_hang,
                   COALESCE(SUM(ct.so_luong), 0)               AS tong_so_luong,
                   COALESCE(SUM(ct.so_luong * ct.don_gia_nhap), 0) AS tong_tien
            FROM pnk p
            JOIN taikhoan nv  ON nv.email  = p.email_nhan_vien
            JOIN taikhoan ncc ON ncc.email = p.email_nha_cung_cap
            LEFT JOIN ctpnk ct ON ct.stt_pnk = p.stt
            """;
    private static final String GROUP = """
             GROUP BY p.stt, nv.ho_ten, ncc.ho_ten
            """;

    private static final RowMapper<PhieuNhap> MAPPER = (rs, i) -> new PhieuNhap(
            rs.getInt("stt"), rs.getTimestamp("ngay_gio").toLocalDateTime(),
            rs.getString("trang_thai"), rs.getString("ghi_chu"),
            rs.getString("email_nhan_vien"), rs.getString("ten_nhan_vien"),
            rs.getString("email_nha_cung_cap"), rs.getString("ten_nha_cung_cap"),
            rs.getInt("so_mat_hang"), rs.getInt("tong_so_luong"), rs.getBigDecimal("tong_tien"));

    public List<PhieuNhap> search(String trangThai, String ncc, LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1 ");
        List<Object> args = new ArrayList<>();
        if (trangThai != null && !trangThai.isBlank()) {
            sql.append(" AND p.trang_thai = ? ");
            args.add(trangThai);
        }
        if (ncc != null && !ncc.isBlank()) {
            sql.append(" AND p.email_nha_cung_cap = ? ");
            args.add(ncc);
        }
        if (tuNgay != null) {
            sql.append(" AND p.ngay_gio >= ? ");
            args.add(Timestamp.valueOf(tuNgay.atStartOfDay()));
        }
        if (denNgay != null) {
            sql.append(" AND p.ngay_gio < ? ");
            args.add(Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));
        }
        sql.append(GROUP).append(" ORDER BY p.stt DESC");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    public List<PhieuNhap> findGanNhat(int limit) {
        return jdbc.query(SELECT + GROUP + " ORDER BY p.stt DESC LIMIT ?", MAPPER, limit);
    }

    public Optional<PhieuNhap> findById(int stt) {
        return jdbc.query(SELECT + " WHERE p.stt = ? " + GROUP, MAPPER, stt).stream().findFirst();
    }

    public List<CtPhieuNhap> findChiTiet(int sttPnk) {
        return jdbc.query("""
                SELECT ct.ma_san_pham, sp.ten, sp.don_vi_tinh, ct.so_luong, ct.don_gia_nhap
                FROM ctpnk ct JOIN sp ON sp.ma_san_pham = ct.ma_san_pham
                WHERE ct.stt_pnk = ?
                ORDER BY sp.ten
                """, (rs, i) -> new CtPhieuNhap(rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getInt(4), rs.getBigDecimal(5)), sttPnk);
    }

    /** INSERT PNK va tra ve so phieu (stt) vua sinh boi SERIAL */
    public int insertPhieu(String emailNhanVien, String emailNhaCungCap, String ghiChu, String trangThai) {
        return jdbc.queryForObject("""
                INSERT INTO pnk (ngay_gio, trang_thai, ghi_chu, email_nhan_vien, email_nha_cung_cap)
                VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?)
                RETURNING stt
                """, Integer.class, trangThai, ghiChu, emailNhanVien, emailNhaCungCap);
    }

    public void insertChiTiet(int sttPnk, String maSanPham, int soLuong, BigDecimal donGiaNhap) {
        jdbc.update("""
                INSERT INTO ctpnk (stt_pnk, ma_san_pham, so_luong, don_gia_nhap)
                VALUES (?, ?, ?, ?)
                """, sttPnk, maSanPham, soLuong, donGiaNhap);
    }

    public void updateTrangThai(int stt, String trangThai, String ghiChu) {
        jdbc.update("UPDATE pnk SET trang_thai = ?, ghi_chu = ? WHERE stt = ?", trangThai, ghiChu, stt);
    }

    public int count() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM pnk", Integer.class);
    }

    public BigDecimal tongTienNhap(String trangThaiHuy) {
        return jdbc.queryForObject("""
                SELECT COALESCE(SUM(ct.so_luong * ct.don_gia_nhap), 0)
                FROM ctpnk ct JOIN pnk p ON p.stt = ct.stt_pnk
                WHERE p.trang_thai <> ?
                """, BigDecimal.class, trangThaiHuy);
    }

    public List<String> findDistinctTrangThai() {
        return jdbc.queryForList("SELECT DISTINCT trang_thai FROM pnk ORDER BY 1", String.class);
    }
}
