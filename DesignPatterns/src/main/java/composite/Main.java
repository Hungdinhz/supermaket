package composite;

import java.util.ArrayList;
import java.util.List;

interface FileSystemItem {
    void show(String indent);
}

class File implements FileSystemItem {
    private final String name;
    File(String name) { this.name = name; }

    @Override
    public void show(String indent) {
        System.out.println(indent + "- [File] " + name);
    }
}

class Folder implements FileSystemItem {
    private final String name;
    private final List<FileSystemItem> children = new ArrayList<>();

    Folder(String name) { this.name = name; }
    void add(FileSystemItem item) { children.add(item); }

    @Override
    public void show(String indent) {
        System.out.println(indent + "+ [Folder] " + name);
        for (FileSystemItem item : children) {
            item.show(indent + "   ");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Folder root = new Folder("Workspace");
        root.add(new File("readme.txt"));
        root.add(new File(".gitignore"));

        Folder docs = new Folder("Documents");
        docs.add(new File("de_cuong_mon_hoc.pdf"));
        docs.add(new File("bao_cao_thuc_tap.docx"));

        Folder images = new Folder("Images");
        images.add(new File("photo.png"));
        images.add(new File("logo.png"));
        docs.add(images);

        Folder src = new Folder("SourceCode");
        src.add(new File("Main.java"));
        src.add(new File("AppConfig.java"));

        root.add(docs);
        root.add(src);

        System.out.println("Hien thi chi tiet thu muc con 'Documents':");
        docs.show("");
        System.out.println("----------");

        System.out.println("Hien thi toan bo cay thu muc goc 'Workspace':");
        root.show("");
    }
}
