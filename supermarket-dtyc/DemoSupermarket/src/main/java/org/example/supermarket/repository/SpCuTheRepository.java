package org.example.supermarket.repository;

import org.example.supermarket.model.Models.SpCuThe;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Truy van bang SPCUTHE - tung ca the san pham co ma dinh danh (serial/barcode) rieng */
@Repository
public class SpCuTheRepository {
    private final JdbcTemplate jdbc;

    public SpCuTheRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SELECT = """
            SELECT ct.stt, ct.tt, ct.ma_dinh_danh, ct.ma_san_pham, sp.ten, ct.stt_pnk, ct.stt_don
            FROM spcuthe ct JOIN sp ON sp.ma_san_pham = ct.ma_san_pham
            """;

    private static final RowMapper<SpCuThe> MAPPER = (rs, i) -> new SpCuThe(
            rs.getInt("stt"), rs.getString("tt"), rs.getString("ma_dinh_danh"),
            rs.getString("ma_san_pham"), rs.getString("ten"), rs.getInt("stt_pnk"),
            (Integer) rs.getObject("stt_don"));

    public List<SpCuThe> findByPhieuNhap(int sttPnk) {
        return jdbc.query(SELECT + " WHERE ct.stt_pnk = ? ORDER BY ct.ma_san_pham, ct.stt", MAPPER, sttPnk);
    }

    public List<SpCuThe> findByDon(int sttDon) {
        return jdbc.query(SELECT + " WHERE ct.stt_don = ? ORDER BY ct.ma_san_pham, ct.stt", MAPPER, sttDon);
    }

    public Optional<SpCuThe> findByMa(String maDinhDanh) {
        return jdbc.query(SELECT + " WHERE ct.ma_dinh_danh = ?", MAPPER, maDinhDanh).stream().findFirst();
    }

    /** Goi y serial (tim gan dung) cho o tra cuu */
    public List<SpCuThe> searchMa(String keyword, int limit) {
        return jdbc.query(SELECT + " WHERE ct.ma_dinh_danh ILIKE ? OR sp.ten ILIKE ? ORDER BY ct.stt DESC LIMIT ?",
                MAPPER, "%" + keyword + "%", "%" + keyword + "%", limit);
    }

    public void insert(String maDinhDanh, String maSanPham, int sttPnk, String tt) {
        jdbc.update("""
                INSERT INTO spcuthe (tt, ma_dinh_danh, ma_san_pham, stt_pnk)
                VALUES (?, ?, ?, ?)
                """, tt, maDinhDanh, maSanPham, sttPnk);
    }

    public boolean existsMa(String maDinhDanh) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM spcuthe WHERE ma_dinh_danh = ?",
                Integer.class, maDinhDanh);
        return c != null && c > 0;
    }

    /** Dem serial cua 1 phieu nhap KHONG con trong kho (da ban hoac loi) */
    public int demKhongTrongKhoCuaPhieu(int sttPnk) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM spcuthe WHERE stt_pnk = ? AND tt <> 'TRONG_KHO'",
                Integer.class, sttPnk);
    }

    public void danhDauLoiTheoPhieu(int sttPnk) {
        jdbc.update("UPDATE spcuthe SET tt = 'LOI' WHERE stt_pnk = ? AND tt = 'TRONG_KHO'", sttPnk);
    }

    /**
     * Lay N serial con TRONG_KHO cua 1 san pham theo nguyen tac FIFO (nhap truoc - ban truoc)
     * va KHOA cac dong do lai (FOR UPDATE) de 2 nhan vien khong xac nhan trung serial.
     */
    public List<Integer> layFifoTrongKho(String maSanPham, int soLuong) {
        return jdbc.queryForList("""
                SELECT stt FROM spcuthe
                WHERE ma_san_pham = ? AND tt = 'TRONG_KHO' AND stt_don IS NULL
                ORDER BY stt
                LIMIT ?
                FOR UPDATE
                """, Integer.class, maSanPham, soLuong);
    }

    public void ganVaoDon(int sttSerial, int sttDon) {
        jdbc.update("UPDATE spcuthe SET tt = 'DA_BAN', stt_don = ? WHERE stt = ?", sttDon, sttSerial);
    }

    /**
     * Bo sung ma dinh danh cho cac phieu nhap du lieu mau ban dau (neu bang SPCUTHE
     * ban dau chi moi chen vai dong mau ma chua du theo CTPNK.so_luong).
     */
    public void boSungSerialTuPhieuNhapCu(String maSanPham, int soLuongCanThem) {
        List<Integer> pnks = jdbc.queryForList("""
                SELECT stt_pnk FROM ctpnk WHERE ma_san_pham = ? ORDER BY stt_pnk LIMIT 1
                """, Integer.class, maSanPham);
        if (pnks.isEmpty()) return;
        int sttPnk = pnks.get(0);
        int seq = 1;
        int daThem = 0;
        while (daThem < soLuongCanThem && seq < 10000) {
            String ma = "SN-%s-%04d".formatted(maSanPham, seq++);
            if (!existsMa(ma)) {
                insert(ma, maSanPham, sttPnk, "TRONG_KHO");
                daThem++;
            }
        }
    }

    /** Huy don: tra serial ve kho */
    public int traVeKho(int sttDon) {
        return jdbc.update("UPDATE spcuthe SET tt = 'TRONG_KHO', stt_don = NULL WHERE stt_don = ?", sttDon);
    }
}
