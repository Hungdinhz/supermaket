package org.example.supermarket.web;

import org.example.supermarket.model.Forms.Dong;
import org.example.supermarket.model.Forms.PhieuNhapForm;
import org.example.supermarket.model.Models.PhieuNhap;
import org.example.supermarket.repository.PhieuNhapRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.repository.SpCuTheRepository;
import org.example.supermarket.repository.TaiKhoanRepository;
import org.example.supermarket.service.BusinessException;
import org.example.supermarket.service.NhapKhoService;
import org.example.supermarket.service.TrangThai;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/** MODULE 1 - NHAP KHO */
@Controller
@RequestMapping("/nhap-kho")
public class NhapKhoController {
    private final NhapKhoService service;
    private final PhieuNhapRepository phieuNhapRepo;
    private final SpCuTheRepository spCuTheRepo;
    private final SanPhamRepository sanPhamRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public NhapKhoController(NhapKhoService service, PhieuNhapRepository phieuNhapRepo,
                             SpCuTheRepository spCuTheRepo, SanPhamRepository sanPhamRepo,
                             TaiKhoanRepository taiKhoanRepo) {
        this.service = service;
        this.phieuNhapRepo = phieuNhapRepo;
        this.spCuTheRepo = spCuTheRepo;
        this.sanPhamRepo = sanPhamRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @ModelAttribute("menu")
    public String menu() {
        return "nhap-kho";
    }

    /** Danh sach phieu nhap + bo loc */
    @GetMapping
    public String list(@RequestParam(required = false) String trangThai,
                       @RequestParam(required = false) String ncc,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
                       Model model) {
        var ds = phieuNhapRepo.search(trangThai, ncc, tuNgay, denNgay);
        model.addAttribute("ds", ds);
        model.addAttribute("tongTien", ds.stream().filter(p -> !TrangThai.PNK_DA_HUY.equals(p.trangThai()))
                .map(PhieuNhap::tongTien).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        model.addAttribute("dsNcc", taiKhoanRepo.findByLoai(TrangThai.NHA_CUNG_CAP));
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("ncc", ncc);
        model.addAttribute("tuNgay", tuNgay);
        model.addAttribute("denNgay", denNgay);
        return "nhap-kho/list";
    }

    /** Form lap phieu moi */
    @GetMapping("/moi")
    public String form(Model model) {
        PhieuNhapForm f = new PhieuNhapForm();
        f.getChiTiet().add(new Dong());
        return showForm(f, model);
    }

    @PostMapping
    public String save(@ModelAttribute("form") PhieuNhapForm form, Model model, RedirectAttributes ra) {
        try {
            int stt = service.lapPhieuNhap(form);
            ra.addFlashAttribute("success", "Đã lập phiếu nhập #" + stt
                    + " – tồn kho đã được cộng và mã định danh đã được sinh cho từng sản phẩm.");
            return "redirect:/nhap-kho/" + stt;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            if (form.getChiTiet().isEmpty()) form.getChiTiet().add(new Dong());
            return showForm(form, model);
        }
    }

    private String showForm(PhieuNhapForm f, Model model) {
        model.addAttribute("form", f);
        model.addAttribute("dsNhanVien", taiKhoanRepo.findByLoai(TrangThai.NHAN_VIEN, TrangThai.ADMIN));
        model.addAttribute("dsNcc", taiKhoanRepo.findByLoai(TrangThai.NHA_CUNG_CAP));
        model.addAttribute("dsSanPham", sanPhamRepo.findAll());
        return "nhap-kho/form";
    }

    /** Chi tiet phieu: CTPNK + danh sach SPCUTHE da sinh */
    @GetMapping("/{stt}")
    public String detail(@PathVariable int stt, Model model) {
        PhieuNhap p = phieuNhapRepo.findById(stt)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu nhập #" + stt));
        model.addAttribute("p", p);
        model.addAttribute("chiTiet", phieuNhapRepo.findChiTiet(stt));
        model.addAttribute("serials", spCuTheRepo.findByPhieuNhap(stt));
        model.addAttribute("coTheHuy", !TrangThai.PNK_DA_HUY.equals(p.trangThai())
                && spCuTheRepo.demKhongTrongKhoCuaPhieu(stt) == 0);
        return "nhap-kho/detail";
    }

    @PostMapping("/{stt}/huy")
    public String huy(@PathVariable int stt, @RequestParam(required = false) String lyDo, RedirectAttributes ra) {
        try {
            service.huyPhieuNhap(stt, lyDo);
            ra.addFlashAttribute("success", "Đã hủy phiếu nhập #" + stt + ", tồn kho đã được trừ lại.");
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/nhap-kho/" + stt;
    }
}
