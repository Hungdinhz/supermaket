package org.example.supermarket.repository;

import org.example.supermarket.model.Models.SanPham;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy van bang SP (kem thong ke SPCUTHE) */
@Repository
public class SanPhamRepository {
    private final JdbcTemplate jdbc;

    public SanPhamRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT sp.*,
                   COUNT(ct.stt) FILTER (WHERE ct.tt = 'TRONG_KHO') AS serial_trong_kho,
                   COUNT(ct.stt) FILTER (WHERE ct.tt = 'DA_BAN')    AS serial_da_ban,
                   COUNT(ct.stt) FILTER (WHERE ct.tt = 'LOI')       AS serial_loi
            FROM sp
            LEFT JOIN spcuthe ct ON ct.ma_san_pham = sp.ma_san_pham
            """;

    private static final RowMapper<SanPham> MAPPER = (rs, i) -> new SanPham(
            rs.getString("ma_san_pham"), rs.getString("ten"), rs.getBigDecimal("gia_ban"),
            rs.getInt("so_luong"), rs.getString("don_vi_tinh"), rs.getString("trang_thai"),
            rs.getString("mo_ta"), rs.getString("hinh_anh"), rs.getString("ten_loai"),
            rs.getString("ten_thuong_hieu"), rs.getInt("serial_trong_kho"),
            rs.getInt("serial_da_ban"), rs.getInt("serial_loi"));

    public List<SanPham> findAll(String keyword, String loai) {
        StringBuilder sql = new StringBuilder(SELECT).append(" WHERE 1=1 ");
        List<Object> args = new java.util.ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (sp.ten ILIKE ? OR sp.ma_san_pham ILIKE ?) ");
            args.add("%" + keyword.trim() + "%");
            args.add("%" + keyword.trim() + "%");
        }
        if (loai != null && !loai.isBlank()) {
            sql.append(" AND sp.ten_loai = ? ");
            args.add(loai);
        }
        sql.append(" GROUP BY sp.ma_san_pham ORDER BY sp.ten_loai, sp.ten");
        return jdbc.query(sql.toString(), MAPPER, args.toArray());
    }

    public List<SanPham> findAll() {
        return findAll(null, null);
    }

    public Optional<SanPham> findById(String ma) {
        return jdbc.query(SELECT + " WHERE sp.ma_san_pham = ? GROUP BY sp.ma_san_pham", MAPPER, ma)
                .stream().findFirst();
    }

    public List<SanPham> findSapHetHang(int nguong) {
        return jdbc.query(SELECT + " WHERE sp.so_luong < ? GROUP BY sp.ma_san_pham ORDER BY sp.so_luong LIMIT 8",
                MAPPER, nguong);
    }

    public List<String> findAllLoai() {
        return jdbc.queryForList("SELECT ten_loai FROM loaisp ORDER BY ten_loai", String.class);
    }

    /** Cong ton kho (khi nhap hang / huy don) */
    public void congTonKho(String ma, int soLuong) {
        jdbc.update("UPDATE sp SET so_luong = so_luong + ? WHERE ma_san_pham = ?", soLuong, ma);
    }

    /**
     * Tru ton kho (khi xac nhan don / huy phieu nhap).
     * Chi tru khi du hang -> tra ve false neu khong du (tranh ton kho am).
     */
    public boolean truTonKho(String ma, int soLuong) {
        return jdbc.update("""
                UPDATE sp SET so_luong = so_luong - ?
                WHERE ma_san_pham = ? AND so_luong >= ?
                """, soLuong, ma, soLuong) == 1;
    }
}
