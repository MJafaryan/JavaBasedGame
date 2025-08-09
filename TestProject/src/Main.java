import models.user.Colony;
import models.user.User;

public class Main {
    public static void main(String[] args) {
        User user1 = new User("Test1", "1234");
        Colony colony1 = new Colony("Iran", user1, "persian", 0, 0);
        System.out.println("1:" + colony1.getColonyName());
        Colony colony2 = new Colony(user1);
        System.out.println("2:" + colony2.getColonyName());
    }
}
