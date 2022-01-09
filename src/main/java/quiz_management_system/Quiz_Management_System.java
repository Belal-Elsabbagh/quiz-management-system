package quiz_management_system;

import java.util.ArrayList;
import java.util.Scanner;

public class Quiz_Management_System
{
    private static User activeUser;

    public static User getActiveUser() { return activeUser; }
    public static void setActiveUser(User activeUser) { Quiz_Management_System.activeUser = activeUser; }

    public static void main(String[] args)
    {
        new DataHandler();
        activeUser = new User("", "");
        activeUser.listMenu();
        //buildData();
    }


    public static void buildData()
    {
        DataHandler.userData = new ArrayList<>();
        DataHandler.quizData = new ArrayList<>();

        User u1 = new Student("Belal", "123", User.Access.STUDENT);
        u1.setUserID(u1.hashCode());
        DataHandler.userData.add(u1);
        User u2 = new Teacher("Adel", "123", User.Access.TEACHER);
        u2.setUserID(u2.getUserID());
        DataHandler.userData.add(u2);
        User u3 = new Admin("Mohamed", "123", User.Access.ADMIN);
        u3.setUserID(u3.hashCode());
        DataHandler.userData.add(u3);

        Quiz newQuiz = new Quiz("2020-MID", "Planets of the Solar System", (Teacher) u2, 1);
        DataHandler.quizData.add(newQuiz);
        ((Student) u1).createNewAttempt(newQuiz);
        DataHandler.save();
    }
}
