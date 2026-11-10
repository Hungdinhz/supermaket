package org.example.supermarket.web;

import org.example.supermarket.model.Models.ThongKe;
import org.example.supermarket.repository.DonRepository;
import org.example.supermarket.repository.PhieuNhapRepository;
import org.example.supermarket.repository.SanPhamRepository;
import org.example.supermarket.service.TrangThai;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Trang chu - so lieu tong quan */
@Controller
public class DashboardController {
    private final JdbcTemplate jdbc;
    private final SanPhamRepository sanPhamRepo;
    private final PhieuNhapRepository phieuNhapRepo;
    private final DonRepository donRepo;

    public DashboardController(JdbcTemplate jdbc, SanPhamRepository sanPhamRepo,
                               PhieuNhapRepository phieuNhapRepo, DonRepository donRepo) {
        this.jdbc = jdbc;
        this.sanPhamRepo = sanPhamRepo;
        this.phieuNhapRepo = phieuNhapRepo;
        this.donRepo = donRepo;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        Integer soSp = jdbc.queryForObject("SELECT COUNT(*) FROM sp", Integer.class);
        Long tonKho = jdbc.queryForObject("SELECT COALESCE(SUM(so_luong),0) FROM sp", Long.class);
        Integer choXn = jdbc.queryForObject("SELECT COUNT(*) FROM don WHERE trang_thai = ?",
                Integer.class, TrangThai.DON_CHO_XAC_NHAN);
        model.addAttribute("tk", new ThongKe(soSp, tonKho, choXn,
                donRepo.doanhThu(TrangThai.DON_HOAN_THANH),
                phieuNhapRepo.count(), phieuNhapRepo.tongTienNhap(TrangThai.PNK_DA_HUY)));
        model.addAttribute("sapHet", sanPhamRepo.findSapHetHang(10));
        model.addAttribute("phieuGanNhat", phieuNhapRepo.findGanNhat(5));
        model.addAttribute("donGanNhat", donRepo.findGanNhat(5));
        model.addAttribute("demDon", donRepo.demTheoTrangThai());
        model.addAttribute("menu", "dashboard");
        return "dashboard";
    }
}
