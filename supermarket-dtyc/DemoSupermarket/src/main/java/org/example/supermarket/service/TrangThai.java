package org.example.supermarket.service;

import java.util.List;
import java.util.Map;

/** Cac gia tri trang thai dung trong CSDL (khop voi du lieu hien co) */
public final class TrangThai {
    private TrangThai() {}

    // PNK
    public static final String PNK_HOAN_THANH = "HOAN_THANH";
    public static final String PNK_DA_HUY = "DA_HUY";

    // DON
    public static final String DON_CHO_XAC_NHAN = "CHO_XAC_NHAN";
    public static final String DON_DA_XAC_NHAN = "DA_XAC_NHAN";
    public static final String DON_DANG_GIAO = "DANG_GIAO";
    public static final String DON_HOAN_THANH = "HOAN_THANH";
    public static final String DON_DA_HUY = "DA_HUY";
    public static final List<String> DON_QUY_TRINH =
            List.of(DON_CHO_XAC_NHAN, DON_DA_XAC_NHAN, DON_DANG_GIAO, DON_HOAN_THANH);
    public static final List<String> DON_TAT_CA =
            List.of(DON_CHO_XAC_NHAN, DON_DA_XAC_NHAN, DON_DANG_GIAO, DON_HOAN_THANH, DON_DA_HUY);

    // Thanh toan
    public static final String TT_CHUA_THANH_TOAN = "CHUA_THANH_TOAN";
    public static final String TT_DA_THANH_TOAN = "DA_THANH_TOAN";
    public static final Map<String, String> PHUONG_THUC = new java.util.LinkedHashMap<>(Map.of());

    static {
        PHUONG_THUC.put("COD", "Thanh toán khi nhận hàng (COD)");
        PHUONG_THUC.put("TIEN_MAT", "Tiền mặt tại quầy");
        PHUONG_THUC.put("CHUYEN_KHOAN", "Chuyển khoản ngân hàng");
        PHUONG_THUC.put("MOMO", "Ví MoMo");
        PHUONG_THUC.put("VNPAY", "VNPay");
    }

    // SPCUTHE
    public static final String SP_TRONG_KHO = "TRONG_KHO";

    // Loai tai khoan
    public static final String NHAN_VIEN = "NHAN_VIEN";
    public static final String NHA_CUNG_CAP = "NHA_CUNG_CAP";
    public static final String KHACH_HANG = "KHACH_HANG";
    public static final String ADMIN = "ADMIN";
}
