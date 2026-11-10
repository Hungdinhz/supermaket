package org.example.supermarket.web;

import org.example.supermarket.model.Models.CtPhieuNhap;
import org.example.supermarket.model.Models.CtDon;
import org.example.supermarket.model.Models.DiaChi;
import org.example.supermarket.model.Models.SpCuThe;
import org.example.supermarket.model.Models.TruyVetSerial;
import org.example.supermarket.repository.DonRepository;
import org.example.supermarket.repository.PhieuNhapRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.repository.SpCuTheRepository;
import org.example.supermarket.repository.TaiKhoanRepository;
import org.example.supermarket.service.BusinessException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Cac trang phu tro: San pham/Ton kho, Tra cuu serial, API dia chi, xu ly loi chung */
@Controller
public class TraCuuController {
    private final SanPhamRepository sanPhamRepo;
    private final SpCuTheRepository spCuTheRepo;
    private final PhieuNhapRepository phieuNhapRepo;
    private final DonRepository donRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public TraCuuController(SanPhamRepository sanPhamRepo, SpCuTheRepository spCuTheRepo,
                            PhieuNhapRepository phieuNhapRepo, DonRepository donRepo,
                            TaiKhoanRepository taiKhoanRepo) {
        this.sanPhamRepo = sanPhamRepo;
        this.spCuTheRepo = spCuTheRepo;
        this.phieuNhapRepo = phieuNhapRepo;
        this.donRepo = donRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    /** Danh sach san pham & ton kho */
    @GetMapping("/san-pham")
    public String sanPham(@RequestParam(required = false) String q,
                          @RequestParam(required = false) String loai, Model model) {
        model.addAttribute("ds", sanPhamRepo.findAll(q, loai));
        model.addAttribute("dsLoai", sanPhamRepo.findAllLoai());
        model.addAttribute("q", q);
        model.addAttribute("loai", loai);
        model.addAttribute("menu", "san-pham");
        return "san-pham/list";
    }

    /** Tra cuu 1 ma dinh danh: truy vet PNK nguon -> DON ban */
    @GetMapping("/tra-cuu")
    public String traCuu(@RequestParam(required = false) String ma, Model model) {
        model.addAttribute("menu", "tra-cuu");
        model.addAttribute("ma", ma);
        if (ma != null && !ma.isBlank()) {
            String m = ma.trim();
            var found = spCuTheRepo.findByMa(m);
            if (found.isPresent()) {
                SpCuThe s = found.get();
                var pnk = phieuNhapRepo.findById(s.sttPnk()).orElse(null);
                var giaNhap = phieuNhapRepo.findChiTiet(s.sttPnk()).stream()
                        .filter(c -> c.maSanPham().equals(s.maSanPham())).map(CtPhieuNhap::donGiaNhap)
                        .findFirst().orElse(null);
                var don = s.sttDon() == null ? null : donRepo.findById(s.sttDon()).orElse(null);
                var giaBan = s.sttDon() == null ? null : donRepo.findChiTiet(s.sttDon()).stream()
                        .filter(c -> c.maSanPham().equals(s.maSanPham())).map(CtDon::donGiaBan)
                        .findFirst().orElse(null);
                model.addAttribute("kq", new TruyVetSerial(s, pnk, giaNhap, don, giaBan));
                model.addAttribute("sp", sanPhamRepo.findById(s.maSanPham()).orElse(null));
            } else {
                model.addAttribute("goiY", spCuTheRepo.searchMa(m, 20));
            }
        } else {
            model.addAttribute("goiY", spCuTheRepo.searchMa("", 12));
        }
        return "tra-cuu";
    }

    /** API JSON: so dia chi cua khach hang (form tao don tu dien dia chi) */
    @GetMapping("/api/dia-chi")
    @ResponseBody
    public List<DiaChi> diaChi(@RequestParam String email) {
        return taiKhoanRepo.findDiaChi(email);
    }

    // ---------------- xu ly loi chung ----------------
    @ControllerAdvice
    public static class ErrorAdvice {
        @ExceptionHandler(BusinessException.class)
        public String business(BusinessException e, Model model) {
            model.addAttribute("title", "Không tìm thấy dữ liệu");
            model.addAttribute("message", e.getMessage());
            return "error-page";
        }

        @ExceptionHandler(DataAccessException.class)
        public String db(DataAccessException e, Model model) {
            model.addAttribute("title", "Lỗi cơ sở dữ liệu");
            model.addAttribute("message", e.getMostSpecificCause().getMessage());
            return "error-page";
        }
    }
}
