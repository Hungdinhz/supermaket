package memento;

class EditorMemento {
    private final String savedText;
    EditorMemento(String savedText) { this.savedText = savedText; }
    String getSavedText() { return savedText; }
}

class Editor {
    private String text = "";

    void type(String words) {
        text += words;
        System.out.println("Da go them: \"" + words + "\"");
    }

    void show() {
        System.out.println(" -> Noi dung van ban hien tai: \"" + text + "\"");
    }

    EditorMemento save() {
        System.out.println("[Memento] Da luu ban chup trang thai (Checkpoint).");
        return new EditorMemento(text);
    }

    void restore(EditorMemento memento) {
        text = memento.getSavedText();
        System.out.println("[Memento] Da khoi phuc (Undo) ve trang thai da luu!");
    }
}

public class Main {
    public static void main(String[] args) {
        Editor editor = new Editor();

        editor.type("Chuong 6: Mau thiet ke phan mem.");
        editor.show();
        EditorMemento checkpoint1 = editor.save();
        System.out.println("----------");

        editor.type(" Phan 1: Mau Memento.");
        editor.show();
        EditorMemento checkpoint2 = editor.save();
        System.out.println("----------");

        editor.type(" [Noi dung go nham can xoa bo!]");
        editor.show();
        System.out.println("----------");

        System.out.println("Thuc hien Undo lan 1 (ve Checkpoint 2):");
        editor.restore(checkpoint2);
        editor.show();
        System.out.println("----------");

        System.out.println("Thuc hien Undo lan 2 (ve Checkpoint 1):");
        editor.restore(checkpoint1);
        editor.show();
    }
}
