package org.example.supermarket.service;

import org.example.supermarket.model.Forms.DonHangForm;
import org.example.supermarket.model.Forms.Dong;
import org.example.supermarket.model.Models.CtDon;
import org.example.supermarket.model.Models.Don;
import org.example.supermarket.model.Models.SanPham;
import org.example.supermarket.model.Models.TaiKhoan;
import org.example.supermarket.repository.DonRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.repository.SpCuTheRepository;
import org.example.supermarket.repository.TaiKhoanRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MODULE 2 - BAN HANG / DON HANG
 * <p>
 * Quy trinh: CHO_XAC_NHAN -> DA_XAC_NHAN -> DANG_GIAO -> HOAN_THANH
 *                 \______________\______> DA_HUY
 * <ul>
 *   <li>Dat don: chi kiem tra du hang, chua tru kho (chua co nhan vien xu ly - email_nhan_vien NULL).</li>
 *   <li>Xac nhan: gan nhan vien, lay serial FIFO -> DA_BAN, tru ton kho.</li>
 *   <li>Huy (truoc khi giao): tra serial ve kho, cong lai ton kho.</li>
 * </ul>
 */
@Service
public class DonHangService {
    private final DonRepository donRepo;
    private final SanPhamRepository sanPhamRepo;
    private final SpCuTheRepository spCuTheRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public DonHangService(DonRepository donRepo, SanPhamRepository sanPhamRepo,
                          SpCuTheRepository spCuTheRepo, TaiKhoanRepository taiKhoanRepo) {
        this.donRepo = donRepo;
        this.sanPhamRepo = sanPhamRepo;
        this.spCuTheRepo = spCuTheRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    /** So luong co the ban = min(ton kho so sach, so ca the thuc te con trong kho) */
    public static int coTheBan(SanPham sp) {
        return sp.coTheBan();
    }

    // =========================== TAO DON ===========================
    @Transactional
    public int taoDon(DonHangForm f) {
        if (f.getEmailKhachHang() == null || f.getEmailKhachHang().isBlank())
            throw new BusinessException("Vui lòng chọn khách hàng.");
        TaiKhoan kh = taiKhoanRepo.findByEmail(f.getEmailKhachHang())
                .orElseThrow(() -> new BusinessException("Khách hàng không tồn tại."));
        if (!TrangThai.KHACH_HANG.equals(kh.loaiTaiKhoan()))
            throw new BusinessException("Tài khoản được chọn không phải khách hàng.");

        String ten = batBuoc(f.getTenNguoiNhan(), "Tên người nhận", 100);
        String sdt = batBuoc(f.getSdtNhan(), "Số điện thoại người nhận", 15);
        if (!sdt.matches("0\\d{9,10}"))
            throw new BusinessException("Số điện thoại người nhận không hợp lệ (bắt đầu bằng 0, gồm 10-11 chữ số).");
        String diaChi = batBuoc(f.getDiaChiNhan(), "Địa chỉ nhận hàng", 255);
        if (!TrangThai.PHUONG_THUC.containsKey(f.getPhuongThucThanhToan()))
            throw new BusinessException("Phương thức thanh toán không hợp lệ.");

        List<Dong> dongs = new ArrayList<>();
        for (Dong d : f.getChiTiet()) if (d != null && !d.isEmpty()) dongs.add(d);
        if (dongs.isEmpty()) throw new BusinessException("Đơn hàng phải có ít nhất 1 sản phẩm.");

        // Kiem tra tung dong + TINH TIEN O SERVER (khong tin so tien tu trinh duyet)
        Set<String> daCo = new HashSet<>();
        List<SanPham> sps = new ArrayList<>();
        BigDecimal tong = BigDecimal.ZERO;
        for (int i = 0; i < dongs.size(); i++) {
            Dong d = dongs.get(i);
            String dong = "Dòng " + (i + 1) + ": ";
            if (d.getMaSanPham() == null || d.getMaSanPham().isBlank())
                throw new BusinessException(dong + "chưa chọn sản phẩm.");
            SanPham sp = sanPhamRepo.findById(d.getMaSanPham())
                    .orElseThrow(() -> new BusinessException(dong + "sản phẩm không tồn tại."));
            if (!daCo.add(sp.maSanPham()))
                throw new BusinessException(dong + "sản phẩm \"" + sp.ten() + "\" bị trùng, hãy tăng số lượng thay vì thêm dòng.");
            if (!"DANG_BAN".equals(sp.trangThai()))
                throw new BusinessException(dong + "sản phẩm \"" + sp.ten() + "\" đang ngừng bán.");
            if (d.getSoLuong() == null || d.getSoLuong() <= 0)
                throw new BusinessException(dong + "số lượng phải lớn hơn 0.");
            if (d.getSoLuong() > coTheBan(sp))
                throw new BusinessException(dong + "\"" + sp.ten() + "\" chỉ còn " + coTheBan(sp)
                        + " " + sp.donViTinh() + " có thể bán (yêu cầu " + d.getSoLuong() + ").");
            sps.add(sp);
            tong = tong.add(sp.giaBan().multiply(BigDecimal.valueOf(d.getSoLuong())));
        }

        String ghiChu = f.getGhiChu() == null || f.getGhiChu().isBlank() ? null : f.getGhiChu().trim();
        int sttDon = donRepo.insertDon(TrangThai.DON_CHO_XAC_NHAN, tong, ten, sdt, diaChi,
                f.getPhuongThucThanhToan(), TrangThai.TT_CHUA_THANH_TOAN, ghiChu, kh.email());
        for (int i = 0; i < dongs.size(); i++) {
            // Don gia ban = gia ban hien tai cua SP (luu lai de sau nay SP doi gia thi don cu khong bi anh huong)
            donRepo.insertChiTiet(sttDon, sps.get(i).maSanPham(), dongs.get(i).getSoLuong(), sps.get(i).giaBan());
        }
        return sttDon;
    }

    // =========================== XAC NHAN ===========================
    @Transactional
    public void xacNhan(int stt, String emailNhanVien) {
        Don don = layDon(stt);
        yeuCauTrangThai(don, TrangThai.DON_CHO_XAC_NHAN);
        if (emailNhanVien == null || emailNhanVien.isBlank())
            throw new BusinessException("Vui lòng chọn nhân viên xử lý đơn.");
        TaiKhoan nv = taiKhoanRepo.findByEmail(emailNhanVien)
                .orElseThrow(() -> new BusinessException("Nhân viên không tồn tại."));
        if (!List.of(TrangThai.NHAN_VIEN, TrangThai.ADMIN).contains(nv.loaiTaiKhoan()))
            throw new BusinessException("Người xử lý phải là nhân viên.");

        for (CtDon ct : donRepo.findChiTiet(stt)) {
            if (!sanPhamRepo.truTonKho(ct.maSanPham(), ct.soLuong()))
                throw new BusinessException("Tồn kho của \"" + ct.tenSanPham() + "\" không đủ (cần " + ct.soLuong() + ").");
            // Lay serial FIFO (nhap truoc - xuat truoc)
            List<Integer> serials = spCuTheRepo.layFifoTrongKho(ct.maSanPham(), ct.soLuong());
            if (serials.size() < ct.soLuong()) {
                // Neu du lieu mau ban dau chua sinh du SPCUTHE cho phieu nhap cu -> bo sung tu CTPNK
                spCuTheRepo.boSungSerialTuPhieuNhapCu(ct.maSanPham(), ct.soLuong() - serials.size());
                serials = spCuTheRepo.layFifoTrongKho(ct.maSanPham(), ct.soLuong());
            }
            if (serials.size() < ct.soLuong())
                throw new BusinessException("Không đủ sản phẩm có mã định danh cho \"" + ct.tenSanPham()
                        + "\". Hãy lập phiếu nhập kho thêm.");
            for (Integer s : serials) spCuTheRepo.ganVaoDon(s, stt);
        }
        donRepo.xacNhan(stt, TrangThai.DON_DA_XAC_NHAN, emailNhanVien);
    }

    // =========================== GIAO HANG ===========================
    @Transactional
    public void giaoHang(int stt, String donViVanChuyen, String maVanDon) {
        Don don = layDon(stt);
        yeuCauTrangThai(don, TrangThai.DON_DA_XAC_NHAN);
        String dv = batBuoc(donViVanChuyen, "Đơn vị vận chuyển", 100);
        String ma = batBuoc(maVanDon, "Mã vận đơn", 50).toUpperCase();
        try {
            donRepo.giaoHang(stt, TrangThai.DON_DANG_GIAO, dv, ma);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("Mã vận đơn \"" + ma + "\" đã được dùng cho đơn khác (mã vận đơn là duy nhất).");
        }
    }

    // =========================== HOAN THANH ===========================
    @Transactional
    public void hoanThanh(int stt) {
        Don don = layDon(stt);
        yeuCauTrangThai(don, TrangThai.DON_DANG_GIAO);
        donRepo.updateTrangThai(stt, TrangThai.DON_HOAN_THANH);
        // Giao thanh cong => da thu tien (COD) / da thanh toan
        donRepo.updateThanhToan(stt, TrangThai.TT_DA_THANH_TOAN);
    }

    @Transactional
    public void xacNhanThanhToan(int stt) {
        Don don = layDon(stt);
        if (TrangThai.DON_DA_HUY.equals(don.trangThai()))
            throw new BusinessException("Đơn đã hủy, không thể xác nhận thanh toán.");
        donRepo.updateThanhToan(stt, TrangThai.TT_DA_THANH_TOAN);
    }

    // =========================== HUY DON ===========================
    @Transactional
    public void huy(int stt) {
        Don don = layDon(stt);
        yeuCauTrangThai(don, TrangThai.DON_CHO_XAC_NHAN, TrangThai.DON_DA_XAC_NHAN);
        if (TrangThai.DON_DA_XAC_NHAN.equals(don.trangThai())) {
            // Da tru kho luc xac nhan -> hoan lai
            spCuTheRepo.traVeKho(stt);
            for (CtDon ct : donRepo.findChiTiet(stt)) sanPhamRepo.congTonKho(ct.maSanPham(), ct.soLuong());
        }
        donRepo.updateTrangThai(stt, TrangThai.DON_DA_HUY);
    }

    // =========================== tien ich ===========================
    private Don layDon(int stt) {
        return donRepo.findById(stt).orElseThrow(() -> new BusinessException("Không tìm thấy đơn hàng #" + stt));
    }

    private void yeuCauTrangThai(Don don, String... hopLe) {
        if (!List.of(hopLe).contains(don.trangThai()))
            throw new BusinessException("Thao tác không hợp lệ: đơn #" + don.stt()
                    + " đang ở trạng thái " + don.trangThai() + ".");
    }

    private String batBuoc(String v, String ten, int maxLen) {
        if (v == null || v.isBlank()) throw new BusinessException(ten + " không được để trống.");
        v = v.trim();
        if (v.length() > maxLen) throw new BusinessException(ten + " tối đa " + maxLen + " ký tự.");
        return v;
    }
}
