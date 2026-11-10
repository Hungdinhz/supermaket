package org.example.supermarket.service;

/** Loi nghiep vu - thong bao than thien hien len giao dien cho nguoi dung */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
