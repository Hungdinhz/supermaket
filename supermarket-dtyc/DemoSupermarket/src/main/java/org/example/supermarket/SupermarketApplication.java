package org.example.supermarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Demo he thong Sieu thi - 2 module chinh:
 *  1. Nhap kho  (PNK, CTPNK, SPCUTHE)
 *  2. Ban hang  (DON, CTDON, SPCUTHE)
 *
 * Chay xong mo trinh duyet: http://localhost:8088
 */
@SpringBootApplication
public class SupermarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupermarketApplication.class, args);
        System.out.println("\n============================================================");
        System.out.println("  WEB DEMO SIEU THI DA SAN SANG: http://localhost:8088");
        System.out.println("============================================================\n");
    }
}
