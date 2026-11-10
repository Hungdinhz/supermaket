package facade;

class Projector {
    void on() { System.out.println("May chieu: da bat nguon."); }
    void setWideScreen() { System.out.println("May chieu: chuyen che do man hinh rong (16:9)."); }
}

class DvdPlayer {
    void on() { System.out.println("Dau phim DVD: da bat va san sang doc dia."); }
    void play(String movie) { System.out.println("Dau phim DVD: dang phat phim \"" + movie + "\"...");}
}

class Amplifier {
    void on() { System.out.println("Ampli: da bat he thong am thanh vom."); }
    void setVolume(int level) { System.out.println("Ampli: dieu chinh am luong muc " + level + "."); }
}

class HomeTheaterFacade {
    private final Projector projector;
    private final DvdPlayer dvd;
    private final Amplifier amp;

    HomeTheaterFacade(Projector projector, DvdPlayer dvd, Amplifier amp) {
        this.projector = projector;
        this.dvd = dvd;
        this.amp = amp;
    }

    void watchMovie(String movie) {
        System.out.println("Chuan bi he thong rap hat tai gia...");
        projector.on();
        projector.setWideScreen();
        dvd.on();
        amp.on();
        amp.setVolume(5);
        dvd.play(movie);
        System.out.println("He thong da san sang! Chuc ban xem phim vui ve.");
    }
}

public class Main {
    public static void main(String[] args) {
        HomeTheaterFacade home = new HomeTheaterFacade(
                new Projector(), new DvdPlayer(), new Amplifier());

        System.out.println("Ca chieu 1: Phim hoat hinh buoi sang");
        home.watchMovie("Doraemon: Nobita va Vung dat ly tuong");
        System.out.println("----------");

        System.out.println("Ca chieu 2: Phim hanh dong buoi toi");
        home.watchMovie("Avengers: Endgame");
    }
}
