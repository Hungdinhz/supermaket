package iterator;

import java.util.ArrayList;
import java.util.List;

class Book {
    private final String title;
    Book(String title) { this.title = title; }
    String getTitle() { return title; }
}

class BookIterator {
    private final List<Book> books;
    private int index = 0;

    BookIterator(List<Book> books) { this.books = books; }
    boolean hasNext() { return index < books.size(); }

    Book next() {
        if (!hasNext()) throw new java.util.NoSuchElementException("Da duyet het sach tren ke!");
        return books.get(index++);
    }
}

class BookShelf {
    private final List<Book> books = new ArrayList<>();
    void add(Book book) {
        books.add(book);
        System.out.println("Da them sach vao ke: \"" + book.getTitle() + "\"");
    }
    BookIterator createIterator() { return new BookIterator(books); }
}

public class Main {
    public static void main(String[] args) {
        BookShelf shelf = new BookShelf();
        shelf.add(new Book("Lap trinh huong doi tuong voi Java"));
        shelf.add(new Book("Cau truc du lieu va Giai thuat"));
        shelf.add(new Book("Co so du lieu quan he"));
        shelf.add(new Book("Mau thiet ke (Design Patterns)"));
        System.out.println("----------");

        System.out.println("Duyet danh sach sach tren ke bang BookIterator:");
        BookIterator iterator = shelf.createIterator();
        int stt = 1;
        while (iterator.hasNext()) {
            System.out.println("Quyen " + (stt++) + ": " + iterator.next().getTitle());
        }
    }
}
