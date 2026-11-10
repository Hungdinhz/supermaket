package org.example.supermarket.web;

import org.example.supermarket.model.Forms.DonHangForm;
import org.example.supermarket.model.Forms.Dong;
import org.example.supermarket.model.Models.Don;
import org.example.supermarket.repository.DonRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.repository.SpCuTheRepository;
import org.example.supermarket.repository.TaiKhoanRepository;
import org.example.supermarket.service.BusinessException;
import org.example.supermarket.service.DonHangService;
import org.example.supermarket.service.TrangThai;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Consumer;

/** MODULE 2 - BAN HANG / DON HANG */
@Controller
@RequestMapping("/don-hang")
public class DonHangController {
    private final DonHangService service;
    private final DonRepository donRepo;
    private final SpCuTheRepository spCuTheRepo;
    private final SanPhamRepository sanPhamRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public DonHangController(DonHangService service, DonRepository donRepo, SpCuTheRepository spCuTheRepo,
                             SanPhamRepository sanPhamRepo, TaiKhoanRepository taiKhoanRepo) {
        this.service = service;
        this.donRepo = donRepo;
        this.spCuTheRepo = spCuTheRepo;
        this.sanPhamRepo = sanPhamRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @ModelAttribute("menu")
    public String menu() {
        return "don-hang";
    }

    @GetMapping
    public String list(@RequestParam(required = false) String trangThai,
                       @RequestParam(required = false) String q, Model model) {
        model.addAttribute("ds", donRepo.search(trangThai, q));
        var dem = donRepo.demTheoTrangThai();
        model.addAttribute("dem", dem);
        model.addAttribute("tongDon", dem.values().stream().mapToInt(Integer::intValue).sum());
        model.addAttribute("dsTrangThai", TrangThai.DON_TAT_CA);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("q", q);
        return "don-hang/list";
    }

    @GetMapping("/moi")
    public String form(Model model) {
        DonHangForm f = new DonHangForm();
        f.getChiTiet().add(new Dong());
        return showForm(f, model);
    }

    @PostMapping
    public String save(@ModelAttribute("form") DonHangForm form, Model model, RedirectAttributes ra) {
        try {
            int stt = service.taoDon(form);
            ra.addFlashAttribute("success", "Đã tạo đơn hàng #" + stt + ". Đơn đang chờ nhân viên xác nhận.");
            return "redirect:/don-hang/" + stt;
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            if (form.getChiTiet().isEmpty()) form.getChiTiet().add(new Dong());
            return showForm(form, model);
        }
    }

    private String showForm(DonHangForm f, Model model) {
        model.addAttribute("form", f);
        model.addAttribute("dsKhachHang", taiKhoanRepo.findByLoai(TrangThai.KHACH_HANG));
        model.addAttribute("dsSanPham", sanPhamRepo.findAll().stream()
                .filter(sp -> "DANG_BAN".equals(sp.trangThai())).toList());
        model.addAttribute("phuongThuc", TrangThai.PHUONG_THUC);
        return "don-hang/form";
    }

    @GetMapping("/{stt}")
    public String detail(@PathVariable int stt, Model model) {
        Don d = donRepo.findById(stt).orElseThrow(() -> new BusinessException("Không tìm thấy đơn hàng #" + stt));
        model.addAttribute("d", d);
        model.addAttribute("chiTiet", donRepo.findChiTiet(stt));
        model.addAttribute("serials", spCuTheRepo.findByDon(stt));
        model.addAttribute("quyTrinh", TrangThai.DON_QUY_TRINH);
        model.addAttribute("buocHienTai", TrangThai.DON_QUY_TRINH.indexOf(d.trangThai()));
        model.addAttribute("dsNhanVien", taiKhoanRepo.findByLoai(TrangThai.NHAN_VIEN, TrangThai.ADMIN));
        return "don-hang/detail";
    }

    @PostMapping("/{stt}/xac-nhan")
    public String xacNhan(@PathVariable int stt, @RequestParam(required = false) String emailNhanVien,
                          RedirectAttributes ra) {
        return action(stt, ra, s -> service.xacNhan(s, emailNhanVien),
                "Đã xác nhận đơn #" + stt + ": đã gán mã định danh (FIFO) và trừ tồn kho.");
    }

    @PostMapping("/{stt}/giao-hang")
    public String giaoHang(@PathVariable int stt, @RequestParam(required = false) String donViVanChuyen,
                           @RequestParam(required = false) String maVanDon, RedirectAttributes ra) {
        return action(stt, ra, s -> service.giaoHang(s, donViVanChuyen, maVanDon),
                "Đơn #" + stt + " đã bàn giao cho đơn vị vận chuyển.");
    }

    @PostMapping("/{stt}/hoan-thanh")
    public String hoanThanh(@PathVariable int stt, RedirectAttributes ra) {
        return action(stt, ra, service::hoanThanh, "Đơn #" + stt + " đã giao thành công.");
    }

    @PostMapping("/{stt}/thanh-toan")
    public String thanhToan(@PathVariable int stt, RedirectAttributes ra) {
        return action(stt, ra, service::xacNhanThanhToan, "Đã ghi nhận thanh toán cho đơn #" + stt + ".");
    }

    @PostMapping("/{stt}/huy")
    public String huy(@PathVariable int stt, RedirectAttributes ra) {
        return action(stt, ra, service::huy, "Đã hủy đơn #" + stt + ". Hàng (nếu đã xuất) đã được trả về kho.");
    }

    private String action(int stt, RedirectAttributes ra, Consumer<Integer> fn, String ok) {
        try {
            fn.accept(stt);
            ra.addFlashAttribute("success", ok);
        } catch (BusinessException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/don-hang/" + stt;
    }
}
