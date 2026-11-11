package mediator;

import java.util.ArrayList;
import java.util.List;

interface ChatMediator {
    void send(String message, User sender);
}

class ChatRoom implements ChatMediator {
    private final List<User> users = new ArrayList<>();

    void addUser(User user) {
        users.add(user);
        System.out.println("[ChatRoom] Thanh vien '" + user.getName() + "' da tham gia phong chat.");
    }

    public void send(String message, User sender) {
        System.out.println(sender.getName() + " gui tin nhan: \"" + message + "\"");
        for (User user : users) {
            if (user != sender) {
                user.receive(sender.getName() + ": " + message);
            }
        }
    }
}

class User {
    private final String name;
    private final ChatMediator mediator;

    User(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    String getName() { return name; }
    void send(String message) { mediator.send(message, this); }
    void receive(String message) {
        System.out.println(" -> [" + name + " nhan tin] " + message);
    }
}

public class Main {
    public static void main(String[] args) {
        ChatRoom room = new ChatRoom();
        User an = new User("An", room);
        User binh = new User("Binh", room);
        User chi = new User("Chi", room);

        room.addUser(an);
        room.addUser(binh);
        room.addUser(chi);
        System.out.println("----------");

        an.send("Chao moi nguoi! Nhom minh hop luc may gio?");
        System.out.println("----------");

        binh.send("Chao An, 8h toi nay tren Google Meet nhe.");
        System.out.println("----------");

        chi.send("Ok, minh da chuan bi xong slide bao cao roi!");
    }
}
