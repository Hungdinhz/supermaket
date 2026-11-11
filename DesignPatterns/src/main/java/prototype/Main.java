package prototype;

import java.util.ArrayList;
import java.util.List;

interface Prototype<T> {
    T copy();
}

class Report implements Prototype<Report> {
    private String title;
    private final List<String> sections;

    Report(String title, List<String> sections) {
        this.title = title;
        this.sections = new ArrayList<>(sections);
    }

    void setTitle(String title) { this.title = title; }
    void addSection(String section) { sections.add(section); }

    @Override
    public Report copy() {
        return new Report(title, sections);
    }

    public String toString() {
        return "Tieu de: \"" + title + "\" | Cac muc: " + sections;
    }
}

public class Main {
    public static void main(String[] args) {
        Report template = new Report("Mau Bao Cao Dinh Ky", List.of("1. Mo dau", "2. Noi dung chinh", "3. Ket luan"));
        System.out.println("Khoi tao ban goc (Template):");
        System.out.println(" -> " + template);
        System.out.println("----------");

        System.out.println("Tao Ban sao 1 (Bao cao Thang 11) va them Phu luc:");
        Report novReport = template.copy();
        novReport.setTitle("Bao cao Doanh thu Thang 11");
        novReport.addSection("4. Phu luc so lieu");
        System.out.println(" -> Ban sao 1: " + novReport);
        System.out.println(" -> Kiem tra ban goc: " + template);
        System.out.println("----------");

        System.out.println("Tao Ban sao 2 (Bao cao Thang 12) va them De xuat:");
        Report decReport = template.copy();
        decReport.setTitle("Bao cao Tong ket Thang 12");
        decReport.addSection("4. De xuat nam moi");
        System.out.println(" -> Ban sao 2: " + decReport);
        System.out.println(" -> Kiem tra ban goc (khong bi anh huong): " + template);
    }
}
