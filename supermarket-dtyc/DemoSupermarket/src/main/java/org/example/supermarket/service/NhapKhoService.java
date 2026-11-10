package org.example.supermarket.service;

import org.example.supermarket.model.Forms.Dong;
import org.example.supermarket.model.Forms.PhieuNhapForm;
import org.example.supermarket.model.Models.CtPhieuNhap;
import org.example.supermarket.model.Models.PhieuNhap;
import org.example.supermarket.model.Models.SanPham;
import org.example.supermarket.model.Models.TaiKhoan;
import org.example.supermarket.repository.PhieuNhapRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.repository.SpCuTheRepository;
import org.example.supermarket.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MODULE 1 - NHAP KHO
 * <p>
 * Lap phieu nhap (PNK) gom nhieu dong chi tiet (CTPNK).
 * Moi dong chi tiet sinh ra dung "so_luong" ca the san pham (SPCUTHE) voi ma dinh danh rieng,
 * dong thoi cong ton kho SP.so_luong.
 * Tat ca nam trong 1 transaction: loi o bat ky buoc nao -> rollback toan bo.
 */
@Service
public class NhapKhoService {
    private final PhieuNhapRepository phieuNhapRepo;
    private final SanPhamRepository sanPhamRepo;
    private final SpCuTheRepository spCuTheRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public NhapKhoService(PhieuNhapRepository phieuNhapRepo, SanPhamRepository sanPhamRepo,
                          SpCuTheRepository spCuTheRepo, TaiKhoanRepository taiKhoanRepo) {
        this.phieuNhapRepo = phieuNhapRepo;
        this.sanPhamRepo = sanPhamRepo;
        this.spCuTheRepo = spCuTheRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @Transactional
    public int lapPhieuNhap(PhieuNhapForm form) {
        // ---- 1. Kiem tra du lieu nhap ----
        kiemTraTaiKhoan(form.getEmailNhanVien(), "Nhân viên lập phiếu", TrangThai.NHAN_VIEN, TrangThai.ADMIN);
        kiemTraTaiKhoan(form.getEmailNhaCungCap(), "Nhà cung cấp", TrangThai.NHA_CUNG_CAP);

        List<Dong> dongs = new ArrayList<>();
        for (Dong d : form.getChiTiet()) {
            if (d != null && !d.isEmpty()) dongs.add(d);
        }
        if (dongs.isEmpty()) {
            throw new BusinessException("Phiếu nhập phải có ít nhất 1 sản phẩm.");
        }
        Set<String> daCo = new HashSet<>();
        for (int i = 0; i < dongs.size(); i++) {
            Dong d = dongs.get(i);
            String dong = "Dòng " + (i + 1) + ": ";
            if (d.getMaSanPham() == null || d.getMaSanPham().isBlank())
                throw new BusinessException(dong + "chưa chọn sản phẩm.");
            SanPham sp = sanPhamRepo.findById(d.getMaSanPham())
                    .orElseThrow(() -> new BusinessException(dong + "sản phẩm không tồn tại."));
            if (!daCo.add(d.getMaSanPham()))
                throw new BusinessException(dong + "sản phẩm \"" + sp.ten()
                        + "\" bị trùng. Mỗi sản phẩm chỉ xuất hiện 1 lần trong phiếu (khóa chính CTPNK).");
            if (d.getSoLuong() == null || d.getSoLuong() <= 0)
                throw new BusinessException(dong + "số lượng phải lớn hơn 0.");
            if (d.getSoLuong() > 500)
                throw new BusinessException(dong + "số lượng tối đa 500/lần nhập (mỗi đơn vị sinh 1 mã định danh).");
            if (d.getDonGia() == null || d.getDonGia().compareTo(BigDecimal.ZERO) < 0)
                throw new BusinessException(dong + "đơn giá nhập phải lớn hơn hoặc bằng 0.");
        }

        // ---- 2. INSERT PNK ----
        String ghiChu = form.getGhiChu() == null || form.getGhiChu().isBlank() ? null : form.getGhiChu().trim();
        int sttPnk = phieuNhapRepo.insertPhieu(form.getEmailNhanVien(), form.getEmailNhaCungCap(),
                ghiChu, TrangThai.PNK_HOAN_THANH);

        for (Dong d : dongs) {
            // ---- 3. INSERT CTPNK ----
            phieuNhapRepo.insertChiTiet(sttPnk, d.getMaSanPham(), d.getSoLuong(), d.getDonGia());

            // ---- 4. Sinh SPCUTHE: moi don vi 1 ma dinh danh ----
            for (int k = 1; k <= d.getSoLuong(); k++) {
                String ma = "SN-%s-P%d-%04d".formatted(d.getMaSanPham(), sttPnk, k);
                spCuTheRepo.insert(ma, d.getMaSanPham(), sttPnk, TrangThai.SP_TRONG_KHO);
            }

            // ---- 5. Cong ton kho ----
            sanPhamRepo.congTonKho(d.getMaSanPham(), d.getSoLuong());
        }
        return sttPnk;
    }

    /**
     * Huy phieu nhap: chi cho phep khi TAT CA hang cua phieu van con trong kho.
     * Cac ca the san pham duoc danh dau LOI (khong xoa) de giu lich su va giu dung
     * luc luong CTPNK ||..|{ SPCUTHE (moi dong chi tiet luon co it nhat 1 ca the).
     */
    @Transactional
    public void huyPhieuNhap(int stt, String lyDo) {
        PhieuNhap p = phieuNhapRepo.findById(stt)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu nhập #" + stt));
        if (TrangThai.PNK_DA_HUY.equals(p.trangThai()))
            throw new BusinessException("Phiếu nhập #" + stt + " đã bị hủy trước đó.");

        int daRaKhoi = spCuTheRepo.demKhongTrongKhoCuaPhieu(stt);
        if (daRaKhoi > 0)
            throw new BusinessException("Không thể hủy: có " + daRaKhoi
                    + " sản phẩm của phiếu này đã bán hoặc bị lỗi. Chỉ hủy được khi toàn bộ hàng còn trong kho.");

        for (CtPhieuNhap ct : phieuNhapRepo.findChiTiet(stt)) {
            if (!sanPhamRepo.truTonKho(ct.maSanPham(), ct.soLuong()))
                throw new BusinessException("Không thể hủy: tồn kho của \"" + ct.tenSanPham()
                        + "\" hiện nhỏ hơn số lượng đã nhập (" + ct.soLuong() + ").");
        }
        spCuTheRepo.danhDauLoiTheoPhieu(stt);

        String ghiChu = (p.ghiChu() == null ? "" : p.ghiChu() + " | ")
                + "Hủy: " + (lyDo == null || lyDo.isBlank() ? "không ghi lý do" : lyDo.trim());
        if (ghiChu.length() > 255) ghiChu = ghiChu.substring(0, 255);
        phieuNhapRepo.updateTrangThai(stt, TrangThai.PNK_DA_HUY, ghiChu);
    }

    private void kiemTraTaiKhoan(String email, String vaiTro, String... loaiHopLe) {
        if (email == null || email.isBlank())
            throw new BusinessException("Vui lòng chọn " + vaiTro.toLowerCase() + ".");
        TaiKhoan tk = taiKhoanRepo.findByEmail(email)
                .orElseThrow(() -> new BusinessException(vaiTro + " không tồn tại."));
        if (!List.of(loaiHopLe).contains(tk.loaiTaiKhoan()))
            throw new BusinessException(vaiTro + " phải là tài khoản loại " + String.join("/", loaiHopLe) + ".");
    }
}
