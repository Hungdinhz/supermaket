package org.example.supermarket.web;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

/**
 * Tien ich hien thi dung trong Thymeleaf: ${@ui.label(x)}, ${@ui.color(x)}, ${@ui.money(x)}
 */
@Component("ui")
public class UiHelper {

    private static final Map<String, String> LABEL = Map.ofEntries(
            Map.entry("HOAN_THANH", "Hoàn thành"),
            Map.entry("DA_HUY", "Đã hủy"),
            Map.entry("CHO_XAC_NHAN", "Chờ xác nhận"),
            Map.entry("DA_XAC_NHAN", "Đã xác nhận"),
            Map.entry("DANG_GIAO", "Đang giao"),
            Map.entry("CHUA_THANH_TOAN", "Chưa thanh toán"),
            Map.entry("DA_THANH_TOAN", "Đã thanh toán"),
            Map.entry("TRONG_KHO", "Trong kho"),
            Map.entry("DA_BAN", "Đã bán"),
            Map.entry("LOI", "Lỗi / Hủy"),
            Map.entry("DANG_BAN", "Đang bán"),
            Map.entry("NGUNG_BAN", "Ngừng bán"),
            Map.entry("COD", "COD"),
            Map.entry("TIEN_MAT", "Tiền mặt"),
            Map.entry("CHUYEN_KHOAN", "Chuyển khoản"),
            Map.entry("MOMO", "MoMo"),
            Map.entry("VNPAY", "VNPay"),
            Map.entry("NHAN_VIEN", "Nhân viên"),
            Map.entry("KHACH_HANG", "Khách hàng"),
            Map.entry("NHA_CUNG_CAP", "Nhà cung cấp"),
            Map.entry("ADMIN", "Quản trị")
    );

    private static final Map<String, String> COLOR = Map.ofEntries(
            Map.entry("HOAN_THANH", "success"),
            Map.entry("DA_HUY", "danger"),
            Map.entry("CHO_XAC_NHAN", "warning"),
            Map.entry("DA_XAC_NHAN", "info"),
            Map.entry("DANG_GIAO", "primary"),
            Map.entry("CHUA_THANH_TOAN", "secondary"),
            Map.entry("DA_THANH_TOAN", "success"),
            Map.entry("TRONG_KHO", "success"),
            Map.entry("DA_BAN", "primary"),
            Map.entry("LOI", "danger"),
            Map.entry("DANG_BAN", "success"),
            Map.entry("NGUNG_BAN", "secondary")
    );

    private static final Map<String, String> ICON = Map.of(
            "CHO_XAC_NHAN", "bi-hourglass-split",
            "DA_XAC_NHAN", "bi-clipboard-check",
            "DANG_GIAO", "bi-truck",
            "HOAN_THANH", "bi-check-circle",
            "DA_HUY", "bi-x-circle"
    );

    public String label(String code) {
        return code == null ? "" : LABEL.getOrDefault(code, code);
    }

    public String color(String code) {
        return code == null ? "secondary" : COLOR.getOrDefault(code, "secondary");
    }

    public String icon(String code) {
        return code == null ? "bi-circle" : ICON.getOrDefault(code, "bi-circle");
    }

    /** 1250000 -> "1.250.000 ₫" */
    public String money(BigDecimal v) {
        if (v == null) return "0 ₫";
        return number(v) + " ₫";
    }

    public String number(Number v) {
        if (v == null) return "0";
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        return new DecimalFormat("#,##0", s).format(v);
    }
}
