package quiz_management_system.UserType;

import quiz_management_system.DataHandler;
import quiz_management_system.GUI.LoginWindow;
import quiz_management_system.Interactive;

import java.io.Serial;
import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable, Interactive
{
    @Serial
    private static final long serialVersionUID = 1L;

    private int userID;
    private String username, password;
    private Access accessLevel;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.accessLevel = Access.valueOf(0);
    }

    public User(String username, String password, int accessLevel)
    {
        this.username = username;
        this.password = password;
        this.accessLevel = Access.valueOf(accessLevel);
    }

    public static User login(String inUsername, String inPassword)
    {
        User newUser = new User(inUsername, inPassword);
        return DataHandler.hasUser(newUser);
    }

    public String getUsername()
    {
        return username;
    }

    public int getUserID()
    {
        return userID;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    public String getPassword()
    {
        return password;
    }

    @Override
    public int listMenu() throws ParseException
    {
        new LoginWindow().constructWindow();
        return 0;
    }

    enum Access
    {
        NONE(0), STUDENT(1), TEACHER(2), ADMIN(3);

        private static Map map = new HashMap<>();

        static
        {
            for (Access aLevel : Access.values())
            {
                map.put(aLevel.value, aLevel);
            }
        }

        private int value;

        private Access(int value)
        {
            this.value = value;
        }

        public static Access valueOf(int pageType)
        {
            return (Access) map.get(pageType);
        }

        public int getValue()
        {
            return value;
        }
    }
}