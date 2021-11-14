package quiz_management_system;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import static quiz_management_system.FileHandler.readFileInObject;

public class User implements Serializable
{
    //default serialVersion id
    private static final long serialVersionUID = 1L;
    

    private int userID;
    private String username;
    private String password;
    private int accessLevel;
    public User()
    {
        accessLevel = -1;
    }
    
    public User(int accessLevel)
    {
        this.accessLevel = accessLevel;
    }
    
    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.accessLevel = -1;
    }

    public User(String username, String password, int accessLevel)
    {
        this.username = username;
        this.password = password;
        this.accessLevel = accessLevel;
    }    
    public User(User og)
    {
        this.userID = og.userID;
        this.username = og.username;
        this.password = og.password;
        this.accessLevel = og.accessLevel;
    }

    public void setUserID(int userID)
    {
        this.userID = userID;
    }

    @Override
    public String toString()
    {
        return "User{" + "userID=" + userID + ", username=" + username + ", password=" + password + ", accessLevel=" + accessLevel + '}';
    }
    
    public int getUserID()
    {
        return userID;
    }

    public int getAccessLevel()
    {
        return accessLevel;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }
    
    public int checkLogin(ArrayList<User> userData)
    {
        int status = 0;
        
        for(User i : userData)
        {
            if(username.equals(i.getUsername()))
            {
                status = 1;
                if(password.equals(i.getPassword()))
                {
                    status = 2;
                    this.userID = i.getUserID();
                    this.accessLevel = i.getAccessLevel();
                }
            }
        }
        //traverse through user records
        //check if username exists, status = 1
        //check if corresponding password is equal to input, status = 2
        return status;
    }

}
